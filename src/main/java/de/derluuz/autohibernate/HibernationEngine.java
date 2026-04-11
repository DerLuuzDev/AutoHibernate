package de.derluuz.autohibernate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.derluuz.autohibernate.mixin.ServerStatusAccessor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

/**
 * Central controller that manages the hibernation lifecycle.
 * <p>
 * Uses a simple {@link AtomicBoolean} flag to track whether the server
 * is currently hibernating, and persists user preferences as JSON.
 */
public final class HibernationEngine {

    // ── Server reference ───────────────────────────────────────────────
    public static volatile @Nullable MinecraftServer server = null;

    // ── State flags ────────────────────────────────────────────────────
    private static final AtomicBoolean hibernating = new AtomicBoolean(true);
    private static final AtomicBoolean autoPause = new AtomicBoolean(true);

    // ── Config persistence ─────────────────────────────────────────────
    private static final Path CONFIG_FILE = Paths.get("config", "autohibernate.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // ────────────────────────────────────────────────────────────────────
    //  Lifecycle
    // ────────────────────────────────────────────────────────────────────

    /** Called once during mod init to restore persisted preferences. */
    public static void initialize() {
        loadConfig();
    }

    // ────────────────────────────────────────────────────────────────────
    //  Auto-pause setting
    // ────────────────────────────────────────────────────────────────────

    public static boolean isAutoPauseEnabled() {
        return autoPause.get();
    }

    public static void setAutoPauseEnabled(boolean enabled) {
        autoPause.set(enabled);
        AutoHibernate.LOG.info("Auto-pause is now {}", enabled ? "enabled" : "disabled");
        persistConfig();

        // When the user turns auto-pause off, wake the server immediately
        if (!enabled) {
            wake("Auto-pause was disabled");
        }
    }

    // ────────────────────────────────────────────────────────────────────
    //  Hibernation queries
    // ────────────────────────────────────────────────────────────────────

    public static boolean isHibernating() {
        MinecraftServer srv = server;
        return srv != null && hibernating.get() && srv.isRunning();
    }

    // ────────────────────────────────────────────────────────────────────
    //  Sleep / Wake
    // ────────────────────────────────────────────────────────────────────

    /**
     * Requests the server to enter hibernation after the current tick
     * finishes processing.
     */
    public static void sleep(String reason) {
        MinecraftServer srv = server;
        if (srv == null || !srv.isRunning()) return;

        if (hibernating.getAndSet(true)) {
            // Already hibernating – nothing to do
            return;
        }

        AutoHibernate.LOG.info("Entering hibernation: {}", reason);

        // Force a status refresh so the server-list reflects the paused state
        ((ServerStatusAccessor) srv).setLastServerStatus(0);

        // Persist world data before going to sleep
        srv.execute(() -> {
            try {
                srv.saveEverything(false, false, false);
            } catch (NoSuchMethodError ignored) {
                // Older MC versions without saveEverything()
                srv.getPlayerList().saveAll();
                srv.saveAllChunks(false, false, false);
            }
        });
    }

    /**
     * Wakes the server from hibernation so ticking resumes immediately.
     */
    public static void wake(String reason) {
        MinecraftServer srv = server;
        if (srv == null) return;

        AutoHibernate.LOG.info("Waking up: {}", reason);
        hibernating.set(false);

        // Unpark the server thread so the tick loop continues
        LockSupport.unpark(srv.getRunningThread());
    }

    // ────────────────────────────────────────────────────────────────────
    //  JSON config
    // ────────────────────────────────────────────────────────────────────

    private static void loadConfig() {
        if (!Files.exists(CONFIG_FILE)) {
            AutoHibernate.LOG.info("No config found at {}, using defaults", CONFIG_FILE);
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                autoPause.set(data.autoPauseEnabled);
                if (!data.autoPauseEnabled) {
                    hibernating.set(false);
                }
                AutoHibernate.LOG.info("Config loaded: autoPauseEnabled={}", data.autoPauseEnabled);
            }
        } catch (IOException e) {
            AutoHibernate.LOG.warn("Failed to read config", e);
        }
    }

    private static void persistConfig() {
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_FILE)) {
                GSON.toJson(new ConfigData(autoPause.get()), writer);
            }
            AutoHibernate.LOG.debug("Config persisted");
        } catch (IOException e) {
            AutoHibernate.LOG.warn("Failed to write config", e);
        }
    }

    /** Simple POJO that maps to / from the JSON file. */
    private static final class ConfigData {
        boolean autoPauseEnabled = true;

        ConfigData() {}

        ConfigData(boolean autoPauseEnabled) {
            this.autoPauseEnabled = autoPauseEnabled;
        }
    }
}

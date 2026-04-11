package de.derluuz.autohibernate;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AutoHibernate — Automatically puts your Minecraft server to sleep
 * when no players are connected, reducing CPU usage to near zero.
 */
public final class AutoHibernate implements DedicatedServerModInitializer {

    public static final String MOD_ID = "autohibernate";
    public static final Logger LOG = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitializeServer() {
        LOG.info("AutoHibernate initializing...");

        // Load persisted settings from disk
        HibernationEngine.initialize();

        // Wire up Brigadier commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            HibernateCommand.register(dispatcher);
            LOG.info("AutoHibernate commands registered");
        });

        LOG.info("AutoHibernate ready");
    }
}

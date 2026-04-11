package de.derluuz.autohibernate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Provides the {@code /autohibernate} command tree:
 * <ul>
 *   <li>{@code /autohibernate} — show current status</li>
 *   <li>{@code /autohibernate autopause true|false} — toggle auto-pause</li>
 *   <li>{@code /autohibernate sleep} — force hibernation</li>
 *   <li>{@code /autohibernate wake} — force wake-up</li>
 * </ul>
 */
public final class HibernateCommand {

    private HibernateCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("autohibernate")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(HibernateCommand::status)
                .then(Commands.literal("autopause")
                    .then(Commands.literal("true")
                        .executes(ctx -> toggleAutoPause(ctx, true)))
                    .then(Commands.literal("false")
                        .executes(ctx -> toggleAutoPause(ctx, false))))
                .then(Commands.literal("sleep")
                    .executes(HibernateCommand::forceSleep))
                .then(Commands.literal("wake")
                    .executes(HibernateCommand::forceWake))
        );
    }

    // ── Sub-commands ───────────────────────────────────────────────────

    private static int status(CommandContext<CommandSourceStack> ctx) {
        boolean auto = HibernationEngine.isAutoPauseEnabled();
        boolean sleeping = HibernationEngine.isHibernating();

        String autoIndicator = auto ? "§aOn" : "§cOff";
        String stateIndicator = sleeping ? "§6Sleeping" : "§aAwake";

        ctx.getSource().sendSuccess(
            () -> Component.literal(
                "§7[AutoHibernate] Auto-Pause: " + autoIndicator
                    + " §7| State: " + stateIndicator),
            false);
        return 1;
    }

    private static int toggleAutoPause(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        HibernationEngine.setAutoPauseEnabled(enabled);
        ctx.getSource().sendSuccess(
            () -> Component.literal(
                "§7[AutoHibernate] Auto-Pause: " + (enabled ? "§aOn" : "§cOff")),
            true);
        return 1;
    }

    private static int forceSleep(CommandContext<CommandSourceStack> ctx) {
        if (HibernationEngine.isHibernating()) {
            ctx.getSource().sendFailure(
                Component.literal("§cServer is already hibernating."));
            return 0;
        }
        HibernationEngine.sleep("Manual sleep via /autohibernate");
        ctx.getSource().sendSuccess(
            () -> Component.literal("§7[AutoHibernate] §6Server is going to sleep..."),
            true);
        return 1;
    }

    private static int forceWake(CommandContext<CommandSourceStack> ctx) {
        if (!HibernationEngine.isHibernating()) {
            ctx.getSource().sendFailure(
                Component.literal("§cServer is not hibernating."));
            return 0;
        }
        HibernationEngine.wake("Manual wake via /autohibernate");
        ctx.getSource().sendSuccess(
            () -> Component.literal("§7[AutoHibernate] §aServer woke up!"),
            true);
        return 1;
    }
}

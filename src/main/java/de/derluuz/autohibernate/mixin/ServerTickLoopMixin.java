package de.derluuz.autohibernate.mixin;

import de.derluuz.autohibernate.AutoHibernate;
import de.derluuz.autohibernate.HibernationEngine;

import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The heart of AutoHibernate — intercepts the server tick loop to
 * completely halt processing while hibernating, and spoofs the next-tick
 * time so the watchdog doesn't kill the server.
 */
@Environment(EnvType.SERVER)
@Mixin(MinecraftServer.class)
public abstract class ServerTickLoopMixin extends ReentrantBlockableEventLoop<TickTask> {

    private ServerTickLoopMixin() {
        super(null, false);
    }

    @Shadow
    protected abstract void waitUntilNextTick();

    /**
     * Before each tick, register the server reference and skip the tick
     * entirely if we are hibernating.
     */
    @Inject(method = "tickServer(Ljava/util/function/BooleanSupplier;)V",
            at = @At("HEAD"), cancellable = true)
    private void onTickStart(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        HibernationEngine.server = (MinecraftServer) (Object) this;

        if (HibernationEngine.isHibernating()) {
            ci.cancel();
        }
    }

    /**
     * When the server is about to halt, make sure we wake up first so
     * the shutdown sequence can complete.
     */
    @Inject(method = "halt", at = @At("HEAD"))
    private void onShutdown(boolean wait, CallbackInfo ci) {
        HibernationEngine.wake("Server is shutting down");
    }

    /**
     * Replaces the normal inter-tick wait with a deep sleep loop that
     * only wakes on {@link LockSupport#unpark}.
     */
    @Redirect(
        method = {"runServer", "run"},
        at = @At(value = "INVOKE",
                 target = "Lnet/minecraft/server/MinecraftServer;waitUntilNextTick()V"),
        require = 1
    )
    private void onInterTickWait(@NotNull MinecraftServer instance) {
        ServerTickLoopMixin self = (ServerTickLoopMixin) (Object) instance;

        if (!HibernationEngine.isHibernating()) {
            // Not hibernating — behave like vanilla
            self.waitUntilNextTick();
            return;
        }

        // Drain any remaining tasks before sleeping
        self.runAllTasks();

        // Suggest a GC pass to keep memory low while idle
        System.gc();

        AutoHibernate.LOG.info("Server is now hibernating");

        while (HibernationEngine.isHibernating()) {
            LockSupport.park("autohibernate:sleeping");
            AutoHibernate.LOG.debug("Server thread briefly woke up");

            // Process queued tasks (e.g. console commands) while still hibernating
            self.runAllTasks();
            if (instance instanceof DedicatedServer dedicated) {
                dedicated.handleConsoleInputs();
            }
        }

        AutoHibernate.LOG.info("Server has woken up");
    }

    /**
     * While hibernating, report the current time as the "next tick time"
     * so the watchdog timer never triggers a crash.
     */
    @Inject(method = "getNextTickTime", at = @At("HEAD"), cancellable = true)
    private void spoofNextTickTime(CallbackInfoReturnable<Long> cir) {
        if (HibernationEngine.isHibernating()) {
            cir.setReturnValue(System.nanoTime());
        }
    }
}

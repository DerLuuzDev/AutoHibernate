package de.derluuz.autohibernate.mixin;

import java.util.concurrent.locks.LockSupport;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Ensures that console commands typed while the server is hibernating
 * actually get processed by waking the server thread briefly.
 */
@Environment(EnvType.SERVER)
@Mixin(DedicatedServer.class)
public abstract class ConsoleWakeupMixin {

    @Inject(method = "handleConsoleInput", at = @At("RETURN"))
    private void onConsoleCommand(String command, CommandSourceStack source, CallbackInfo ci) {
        Thread serverThread = ((MinecraftServer) (Object) this).getRunningThread();
        LockSupport.unpark(serverThread);
    }
}

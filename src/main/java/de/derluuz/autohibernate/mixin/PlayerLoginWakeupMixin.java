package de.derluuz.autohibernate.mixin;

import de.derluuz.autohibernate.HibernationEngine;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Wakes the server the moment a player begins the login handshake,
 * ensuring the server is fully ticking before the player finishes
 * connecting.
 */
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class PlayerLoginWakeupMixin {

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void onLoginStart(CallbackInfo ci) {
        HibernationEngine.wake("Player login handshake started");
    }
}

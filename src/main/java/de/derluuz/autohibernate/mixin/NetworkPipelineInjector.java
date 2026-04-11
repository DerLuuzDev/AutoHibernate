package de.derluuz.autohibernate.mixin;

import de.derluuz.autohibernate.ConnectionMonitor;
import io.netty.channel.Channel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks into the anonymous ChannelInitializer inside ServerConnectionListener
 * to prepend our {@link ConnectionMonitor} to every incoming channel pipeline.
 */
@Environment(EnvType.SERVER)
@Mixin(targets = "net.minecraft.server.network.ServerConnectionListener$1")
public final class NetworkPipelineInjector {

    @Inject(method = "initChannel", at = @At("RETURN"), remap = false)
    private void onChannelInit(Channel channel, CallbackInfo ci) {
        channel.pipeline().addFirst("autohibernate_monitor", new ConnectionMonitor());
    }
}

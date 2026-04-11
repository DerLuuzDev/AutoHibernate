package de.derluuz.autohibernate.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor for the {@code lastServerStatus} field so we can force a
 * server-list status refresh right before the server goes to sleep.
 */
@Mixin(MinecraftServer.class)
public interface ServerStatusAccessor {

    @Accessor
    void setLastServerStatus(long nanoseconds);
}

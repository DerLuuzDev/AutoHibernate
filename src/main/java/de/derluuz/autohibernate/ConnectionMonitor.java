package de.derluuz.autohibernate;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sits at the front of each Netty channel pipeline to track how many
 * network connections are active. When the last connection drops and
 * auto-pause is enabled, the server enters hibernation.
 */
public final class ConnectionMonitor extends ChannelInboundHandlerAdapter {

    private static final AtomicInteger openConnections = new AtomicInteger(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int count = openConnections.incrementAndGet();
        AutoHibernate.LOG.debug("Connection opened ({} active): {}", count, ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        int remaining = openConnections.decrementAndGet();
        AutoHibernate.LOG.debug("Connection closed ({} remaining): {}", remaining, ctx.channel());

        if (remaining <= 0 && HibernationEngine.isAutoPauseEnabled()) {
            HibernationEngine.sleep("All connections closed");
        }

        super.channelInactive(ctx);
    }
}

package org.freeswitch.esl.client.inbound;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * reconnect handler
 */
@ChannelHandler.Sharable
public class ReconnectHandler extends ChannelInboundHandlerAdapter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final int MAX_RETRY = 10;
    private final Client client;
    private int retry = 0;

    public ReconnectHandler(Client client) {
        this.client = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("activate set retry 0");
        retry = 0;

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isOpen()) {
            ctx.close();
        }

        EventLoop eventLoop = ctx.channel().eventLoop();
        retry ++;
        int timeInterval = 5;
        if (retry > MAX_RETRY) {
            timeInterval = 60;
        }

        log.info("after {}s retry {} times ...", timeInterval, retry);
        eventLoop.schedule(() -> {
            try {
                client.connect();
            } catch (Exception e) {
                // ignore
            }
        }, timeInterval, TimeUnit.SECONDS);

        super.channelInactive(ctx);

    }
}

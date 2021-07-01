package org.freeswitch.esl.client.inbound;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.freeswitch.esl.client.transport.message.EslFrameDecoder;

/**
 * End users of the {@link Client} should not need to use this class.
 * <p/>
 * Convenience factory to assemble a Netty processing pipeline for inbound clients.
 */
class InboundChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelHandler handler;
    private final ReconnectHandler reconnectHandler;

    public InboundChannelInitializer(ChannelHandler handler, ReconnectHandler reconnectHandler) {
        this.handler = handler;
        this.reconnectHandler = reconnectHandler;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(reconnectHandler);
        pipeline.addLast("decoder", new EslFrameDecoder(8192));

        // now the inbound client logic
        pipeline.addLast("clientHandler", handler);
        pipeline.addLast("encoder", new StringEncoder());
    }
}

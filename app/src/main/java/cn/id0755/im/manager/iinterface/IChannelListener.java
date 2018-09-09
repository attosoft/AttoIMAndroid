package cn.id0755.im.manager.iinterface;

import io.netty.channel.ChannelHandlerContext;

public interface IChannelListener {
    void channelActive(ChannelHandlerContext ctx);
    void channelInactive(ChannelHandlerContext ctx);
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);
}

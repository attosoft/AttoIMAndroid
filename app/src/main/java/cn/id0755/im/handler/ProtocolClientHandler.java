package cn.id0755.im.handler;

import com.google.protobuf.MessageLite;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.id0755.im.chat.proto.HeartBeat;
import cn.id0755.im.chat.proto.Login;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.im.manager.iinterface.IChannelListener;
import cn.id0755.im.utils.MessageUtil;
import cn.id0755.sdk.android.utils.Log;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;


public class ProtocolClientHandler extends SimpleChannelInboundHandler<Message.MessageData> {
    private final static String TAG = "ProtocolClientHandler";

    private IChannelListener mChannelListener;

    private List<BaseBizHandler> mBizHandlerList = new LinkedList<>();

    /**
     * Creates a client-side handler.
     */
    public ProtocolClientHandler(IChannelListener channelListener) {
        mChannelListener = channelListener;
        init();
    }

    private void init() {
        mBizHandlerList.add(new LoginHandler());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (mChannelListener != null) {
            mChannelListener.channelInactive(ctx);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Log.d(TAG, "userEventTriggered | evt:" + evt.toString());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    HeartBeat.Ping ping = HeartBeat.Ping
                            .newBuilder()
                            .setCmdId(Message.CMD_ID.PING)
                            .build();
                    ctx.channel().writeAndFlush(MessageUtil.wrap(Message.CMD_ID.PING, ping));
                    break;
                case WRITER_IDLE:
                    break;
                case ALL_IDLE:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (mChannelListener != null) {
            mChannelListener.channelActive(ctx);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message.MessageData msg) throws Exception {
        Log.d(TAG, "channelRead0 | msg:" + msg.getCmdId());

        for (BaseBizHandler bizHandler : mBizHandlerList) {
            if (bizHandler.channelRead0(ctx, msg)) {
                break;
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (mChannelListener != null) {
            mChannelListener.exceptionCaught(ctx, cause);
        }
        Log.d(TAG, "exceptionCaught | cause:" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}

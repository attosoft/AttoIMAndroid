package cn.id0755.im.handler;

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

    /**
     * Creates a client-side handler.
     */
    public ProtocolClientHandler(IChannelListener channelListener) {
        mChannelListener = channelListener;
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
//                    handleReaderIdle(ctx);
                    HeartBeat.Ping ping = HeartBeat.Ping
                            .newBuilder()
                            .setCmdId(Message.CMD_ID.PING)
                            .build();
                    ctx.channel().writeAndFlush(MessageUtil.wrap(Message.CMD_ID.PING, ping));
                    break;
                case WRITER_IDLE:
//                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
//                    handleAllIdle(ctx);
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
        switch (msg.getCmdId()) {
            case LOGIN_REQ: {
                Login.LoginRequest loginRequest = Login.LoginRequest.getDefaultInstance()
                        .getParserForType()
                        .parseFrom(msg.getContent());
            }
            break;
            case LOGIN_RESP: {
                Login.LoginResponse loginResponse = Login.LoginResponse.getDefaultInstance()
                        .getParserForType()
                        .parseFrom(msg.getContent());
            }
            break;
            case PING:
                break;
            case PONG:
                break;
            default:
                break;
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
        Log.d(TAG,"exceptionCaught | cause:"+cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
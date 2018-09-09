package cn.id0755.im.test;

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
        if (mChannelListener != null){
            mChannelListener.channelInactive(ctx);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
//                    handleReaderIdle(ctx);
                    HeartBeat.Ping ping = HeartBeat.Ping
                            .newBuilder()
                            .setCmdId(Message.CMD_ID.CMD_PING)
                            .build();
                    ctx.channel().writeAndFlush(MessageUtil.wrap(ping));
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
        if (mChannelListener != null){
            mChannelListener.channelActive(ctx);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message.MessageData msg) throws Exception {
        switch (msg.getCmdId()) {
            case CMD_LOGIN_REQ: {
                Login.LoginRequest loginRequest = Login.LoginRequest.getDefaultInstance()
                        .getParserForType()
                        .parseFrom(msg.getContent());
                Log.e(TAG, loginRequest.toString());
            }
            break;
            case CMD_LOGIN_RESP: {
                Login.LoginResponse loginResponse = Login.LoginResponse.getDefaultInstance()
                        .getParserForType()
                        .parseFrom(msg.getContent());
                Log.e(TAG, loginResponse.toString());
            }
            break;
            case CMD_PING:
                break;
            case CMD_PONG:
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
        if (mChannelListener != null){
            mChannelListener.exceptionCaught(ctx, cause);
        }
        cause.printStackTrace();
        ctx.close();
    }
}

package cn.id0755.im.test;

import cn.id0755.im.chat.proto.Login;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.im.utils.MessageUtil;
import cn.id0755.sdk.android.utils.Log;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class ProtocolClientHandler extends SimpleChannelInboundHandler<Message.MessageData> {
    private final static String TAG = "ProtocolClientHandler";

    /**
     * Creates a client-side handler.
     */
    public ProtocolClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        for (int i = 0; i < 10; i++) {
            ctx.write(MessageUtil.wrap(sendMessageReq(i)));
        }
        ctx.flush();
    }

    private Login.LoginRequest sendMessageReq(int i) {
        Login.LoginRequest.Builder builder = Login.LoginRequest.newBuilder();
        builder.setAccount("13510773022");
        builder.setPassword("2219252");
        return builder.build();
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
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

package cn.id0755.sdk.android.handler;

import cn.id0755.im.chat.proto.Login;
import cn.id0755.im.chat.proto.Message;
import io.netty.channel.ChannelHandlerContext;

public class LoginHandler extends BaseBizHandler<Login.LoginRequest> {
    @Override
    protected void channelRead(ChannelHandlerContext ctx, Login.LoginRequest loginRequest) {

    }

    @Override
    protected Message.CMD_ID getType() {
        return Message.CMD_ID.LOGIN_REQ;
    }

    @Override
    protected Login.LoginRequest getMessageLite() {
        return Login.LoginRequest.getDefaultInstance();
    }

}

package cn.id0755.sdk.android.handler;

import android.os.RemoteException;

import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.chat.proto.Login;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.sdk.android.manager.ConnectionManager;
import io.netty.channel.ChannelHandlerContext;

public class LoginHandler extends BaseBizHandler<Login.LoginResponse> {

    @Override
    protected void channelRead(ChannelHandlerContext ctx, Login.LoginResponse loginResponse) {
        ITaskWrapper taskWrapper = ConnectionManager.getInstance().getTaskById(loginResponse.getTaskId());
        if (taskWrapper != null) {
            try {
                taskWrapper.buf2resp(loginResponse.getAccessToken().getBytes());
                taskWrapper.onTaskEnd(0, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Message.CMD_ID getType() {
        return Message.CMD_ID.LOGIN_RESP;
    }

    @Override
    protected Login.LoginResponse getMessageLite() {
        return Login.LoginResponse.getDefaultInstance();
    }

}

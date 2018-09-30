package cn.id0755.sdk.android.handler;

import android.os.RemoteException;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.chat.proto.Login;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.sdk.android.manager.ConnectionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class LoginHandler extends BaseBizHandler<Login.LoginResponse> {

    @Override
    protected void channelRead(ChannelHandlerContext ctx, Login.LoginResponse loginResponse, ITaskWrapper taskWrapper) {
        ConnectionManager.mWorkerExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    taskWrapper.buf2resp(loginResponse.toByteArray());
                    taskWrapper.onTaskEnd(0, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
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

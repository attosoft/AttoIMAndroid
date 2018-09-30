package cn.id0755.sdk.android.handler;

import android.os.RemoteException;

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
    protected void channelRead(ChannelHandlerContext ctx, Login.LoginResponse loginResponse) {
        Attribute<Map<Integer, ITaskWrapper>> attribute = ctx.channel().attr(AttributeKey.valueOf(ConnectionManager.KEY_TASK));
        Map<Integer, ITaskWrapper> taskWrapperMap = attribute.get();
        ITaskWrapper taskWrapper = null;
        if (taskWrapperMap != null && taskWrapperMap.containsKey(loginResponse.getTaskId())) {
            taskWrapper = taskWrapperMap.remove(loginResponse.getTaskId());
        }
        ITaskWrapper finalTaskWrapper = taskWrapper;
        ConnectionManager.mWorkerExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (finalTaskWrapper != null) {
                    try {
                        finalTaskWrapper.buf2resp(loginResponse.getAccessToken().getBytes());
                        finalTaskWrapper.onTaskEnd(0, 0);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
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

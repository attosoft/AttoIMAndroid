package cn.id0755.sdk.android.handler;

import android.os.RemoteException;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Map;

import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.im.chat.proto.Push;
import cn.id0755.sdk.android.manager.ConnectionManager;
import cn.id0755.sdk.android.manager.iinterface.IPushMessageListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class PushHandler extends BaseBizHandler<Push.Message> {
    @Override
    protected void channelRead(ChannelHandlerContext ctx, Push.Message message, ITaskWrapper taskWrapper) {

    }

    private IPushMessageListener mPushMessageListener;

    public PushHandler(IPushMessageListener listener) {
        this.mPushMessageListener = listener;
    }

    @Override
    public boolean channelRead0(ChannelHandlerContext ctx, Message.MessageData msg) throws InvalidProtocolBufferException {
        if (getType() != msg.getCmdId()) {
            return false;
        }
        final Push.Message message = getMessageLite().getParserForType().parseFrom(msg.getContent());
        ConnectionManager.mWorkerExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (mPushMessageListener != null) {
                    mPushMessageListener.pushMessage(message);
                }
            }
        });

        return true;
    }

    @Override
    protected Message.CMD_ID getType() {
        return Message.CMD_ID.PUSH;
    }

    @Override
    protected Push.Message getMessageLite() {
        return Push.Message.getDefaultInstance();
    }
}

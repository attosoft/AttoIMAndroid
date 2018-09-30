package cn.id0755.sdk.android.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.sdk.android.manager.ConnectionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public abstract class BaseBizHandler<Message extends MessageLite> {
    protected abstract void channelRead(ChannelHandlerContext ctx, Message message, ITaskWrapper taskWrapper);

    protected abstract cn.id0755.im.chat.proto.Message.CMD_ID getType();

    protected abstract Message getMessageLite();

    public boolean channelRead0(ChannelHandlerContext ctx, cn.id0755.im.chat.proto.Message.MessageData msg) throws InvalidProtocolBufferException {
        if (getType() != msg.getCmdId()) {
            return false;
        }
        Message message = (Message) getMessageLite().getParserForType().parseFrom(msg.getContent());
        Attribute<Map<String, ITaskWrapper>> attribute = ctx.channel().attr(AttributeKey.valueOf(ConnectionManager.KEY_TASK));
        Map<String, ITaskWrapper> taskWrapperMap = attribute.get();
        ITaskWrapper taskWrapper = null;
        if (taskWrapperMap != null && taskWrapperMap.containsKey(msg.getSeqId())) {
            taskWrapper = taskWrapperMap.remove(msg.getSeqId());
        }
        channelRead(ctx, message, taskWrapper);
        return true;
    }
}

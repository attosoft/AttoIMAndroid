package cn.id0755.sdk.android.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cn.id0755.im.chat.proto.Message;
import io.netty.channel.ChannelHandlerContext;

public abstract class BaseBizHandler<Message extends MessageLite> {
    protected abstract void channelRead(ChannelHandlerContext ctx, Message message);

    protected abstract cn.id0755.im.chat.proto.Message.CMD_ID getType();

    protected abstract Message getMessageLite();

    public boolean channelRead0(ChannelHandlerContext ctx, cn.id0755.im.chat.proto.Message.MessageData msg) throws InvalidProtocolBufferException {
        if (getType() != msg.getCmdId()) {
            return false;
        }
        Message message = (Message) getMessageLite().getParserForType().parseFrom(msg.getContent());
        channelRead(ctx, message);
        return true;
    }
}

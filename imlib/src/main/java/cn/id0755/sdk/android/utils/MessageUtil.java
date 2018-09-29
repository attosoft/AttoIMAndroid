package cn.id0755.sdk.android.utils;

import cn.id0755.im.chat.proto.Message;

public class MessageUtil {
    public static Message.MessageData wrap(Message.CMD_ID cmd_id, com.google.protobuf.AbstractMessageLite messageLite) {
        Message.MessageData.Builder builder = Message.MessageData.newBuilder();
        builder.setCmdId(cmd_id);
        builder.setContent(messageLite != null ? messageLite.toByteString() : null);
        return builder.build();
    }
}

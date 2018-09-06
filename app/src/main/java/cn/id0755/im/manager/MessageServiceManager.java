package cn.id0755.im.manager;

import com.google.protobuf.AbstractMessageLite;

import cn.id0755.im.chat.proto.Message;
import cn.id0755.im.utils.MessageUtil;

public class MessageServiceManager {
    public void sendMessage(AbstractMessageLite messageLite){
        Message.MessageData messageData = MessageUtil.wrap(messageLite);
    }
}

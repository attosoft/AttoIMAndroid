package cn.id0755.im.handler;

import cn.id0755.im.chat.proto.HeartBeat;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.sdk.android.utils.Log;
import io.netty.channel.ChannelHandlerContext;

public class PongHandler extends BaseBizHandler<HeartBeat.Pong>{
    private final static String TAG = PongHandler.class.getSimpleName();
    @Override
    protected void channelRead(ChannelHandlerContext ctx, HeartBeat.Pong pong) {
        Log.d(TAG,"channelRead : ");
    }

    @Override
    protected Message.CMD_ID getType() {
        return Message.CMD_ID.PONG;
    }

    @Override
    protected HeartBeat.Pong getMessageLite() {
        return HeartBeat.Pong.getDefaultInstance();
    }
}

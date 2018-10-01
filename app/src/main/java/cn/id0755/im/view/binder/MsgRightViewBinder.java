package cn.id0755.im.view.binder;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import cn.id0755.im.view.entity.MsgEntity;
import cn.id0755.im.view.holder.MsgRightVH;
import me.drakeet.multitype.ItemViewBinder;

public class MsgRightViewBinder extends ItemViewBinder<MsgEntity, MsgRightVH> {
    @NonNull
    @Override
    protected MsgRightVH onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new MsgRightVH(inflater.inflate(MsgRightVH.RES_LAYOUT, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull MsgRightVH holder, @NonNull MsgEntity item) {
        holder.getContent().setText(item.getContent());
    }
}

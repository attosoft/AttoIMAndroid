package cn.id0755.im.manager.provider;

import cn.id0755.im.manager.service.IProvider;
import cn.id0755.im.manager.service.IService;

/**
 * Created by andy on 2018/10/10.
 */
public class MsgProvider implements IProvider{
    @Override
    public IService getService() {
        return new IService() {

        };
    }
}

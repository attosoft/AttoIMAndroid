package cn.id0755.im.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.id0755.im.manager.service.IProvider;
import cn.id0755.sdk.android.manager.MsgServiceImpl;

/**
 * Created by andy on 2018/10/10.
 */
public class ServerManager {
    private ServerManager() {
        registerService("MSG", new IProvider() {
            @Override
            public Object getService() {
                return MsgServiceImpl.getInstance();
            }
        });
    }

    private final static ServerManager INSTANCE = new ServerManager();

    private final Map<String, IProvider> providers = new ConcurrentHashMap<>();

    public static ServerManager getInstance() {
        return INSTANCE;
    }

    private void registerService(String name, IProvider provider) {
        providers.put(name, provider);
    }

    public <Service> Service getService(String name) {
        IProvider p = providers.get(name);
        if (p == null) {
            throw new IllegalArgumentException("No provider registered with name:" + name);
        }
        return (Service) (p.getService());
    }
}

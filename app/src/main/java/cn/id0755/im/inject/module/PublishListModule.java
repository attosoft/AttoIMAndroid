package cn.id0755.im.inject.module;

import cn.id0755.im.data.repository.PublishRepository;
import dagger.Module;
import dagger.Provides;

@Module
public class PublishListModule {

    @Provides
    PublishRepository providerPublishRepository(){
        return new PublishRepository();
    }
}

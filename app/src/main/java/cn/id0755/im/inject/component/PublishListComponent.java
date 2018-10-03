package cn.id0755.im.inject.component;

import cn.id0755.im.inject.module.PublishListModule;
import cn.id0755.im.view.viewmodel.PublishListViewModel;
import dagger.Component;

@Component(modules = {PublishListModule.class})
public interface PublishListComponent {
    void inject(PublishListViewModel viewModel);
}

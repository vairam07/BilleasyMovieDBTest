package com.billeasy.billeasymoviedb.app.main;

import com.billeasy.billeasymoviedb.app.ActivityScope;

import dagger.Module;
import dagger.Provides;



@Module
public class MainModule {
    private final MainContract.View mainView;

    MainModule(MainContract.View mainView) {
        this.mainView = mainView;
    }

    @Provides
    @ActivityScope
    MainContract.View provideMainView() {
        return mainView;
    }

}

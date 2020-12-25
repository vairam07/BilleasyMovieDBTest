package com.billeasy.billeasymoviedb.app;

import android.app.Application;


import com.billeasy.billeasymoviedb.api.ApiModule;
import com.billeasy.billeasymoviedb.api.ApiService;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Provides;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(
        modules = {
                AppModule.class,
                ApiModule.class
        }
)
public interface AppComponent {
    Application application();

    ApiService apiService();
    void inject(App app);

}
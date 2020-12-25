package com.billeasy.billeasymoviedb.app.detail;



import com.billeasy.billeasymoviedb.app.ActivityScope;
import com.billeasy.billeasymoviedb.app.AppComponent;
import com.billeasy.billeasymoviedb.app.AppModule;

import dagger.Component;
import dagger.Provides;


@ActivityScope
@Component(
        dependencies = AppComponent.class,
        modules = DetailModule.class
)
interface DetailComponent {

    void inject(DetailActivity DetailActivity);

}

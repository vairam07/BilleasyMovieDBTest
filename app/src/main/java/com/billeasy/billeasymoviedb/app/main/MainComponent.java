package com.billeasy.billeasymoviedb.app.main;



import com.billeasy.billeasymoviedb.app.ActivityScope;
import com.billeasy.billeasymoviedb.app.AppComponent;

import dagger.Component;



@ActivityScope
@Component(
        dependencies = AppComponent.class,
        modules = MainModule.class
)
interface MainComponent {

    void inject (MainActivity mainActivity);

}

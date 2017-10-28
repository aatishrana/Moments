package com.aatishrana.moments.di;

/**
 * Created by Aatish on 6/17/2017.
 */

import com.aatishrana.moments.data.AddDataActivity;
import com.aatishrana.moments.main.MainActivity;
import com.aatishrana.moments.splash.SplashActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent
{
    void inject(SplashActivity splashActivity);

    void inject(AddDataActivity addDataActivity);

    void inject(MainActivity mainActivity);
}

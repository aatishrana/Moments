package com.aatishrana.moments.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Aatish on 6/17/2017.
 */
@Module
public class AppModule
{
    private Context context;
    private String sharedPreferenceName = "Moments_Data";

    public AppModule(Context context)
    {
        this.context = context;
    }

    @Provides
    @Singleton
    Context provideContext()
    {
        return context;
    }

    @Provides
    @Singleton
    Resources provideResources(Context context)
    {
        return context.getResources();
    }

    @Provides
    LayoutInflater provideLayoutInflater(Context context)
    {
        return LayoutInflater.from(context);
    }

    @Provides
    SharedPreferences provideSharedPreference(Context context)
    {
        return context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
    }

    @Provides
    TelephonyManager provideTelephonyManager(Context context)
    {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }
}

package com.aatishrana.moments.splash;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.aatishrana.moments.MyApplication;
import com.aatishrana.moments.data.AddDataActivity;
import com.aatishrana.moments.main.MainActivity;

import javax.inject.Inject;

import static com.aatishrana.moments.utils.SharedData.fromSplash;
import static com.aatishrana.moments.utils.SharedData.isDataPresent;

/**
 * Created by Aatish on 6/17/2017.
 */

public class SplashActivity extends Activity
{
    @Inject
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MyApplication.getAppComponent().inject(this);
        checkState();
    }

    private void checkState()
    {
        if (!sharedPreferences.getBoolean(isDataPresent, false))
        {
            //route to add data screen
            startActivity(AddDataActivity.getIntent(this, false, fromSplash));
        } else
        {
            //route to main screen
            startActivity(MainActivity.getIntent(this, true));
        }
        this.finish();
    }
}

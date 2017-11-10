package com.aatishrana.moments.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aatishrana.moments.MyApplication;
import com.aatishrana.moments.R;
import com.aatishrana.moments.data.AddDataActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.aatishrana.moments.utils.SharedData.dateData;
import static com.aatishrana.moments.utils.SharedData.dateDataD;
import static com.aatishrana.moments.utils.SharedData.format;
import static com.aatishrana.moments.utils.SharedData.fromMain;
import static com.aatishrana.moments.utils.SharedData.isDataPresent;
import static com.aatishrana.moments.utils.SharedData.timeData;

public class MainActivity extends Activity
{
    private Disposable subscriptionLived;
    private Disposable subscriptionWillLive;

    public static Intent getIntent(Context context, boolean isDataAvailable)
    {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(isDataPresent, isDataAvailable);
        return intent;
    }

    @Inject
    SharedPreferences sharedPreferences;
    @BindView(R.id.activity_main_tv_time_lived)
    TextView tvLived;
    @BindView(R.id.activity_main_tv_time_left)
    TextView tvWillLive;
    @BindView(R.id.activity_main_tv_format)
    TextView tvFormat;
    @BindView(R.id.activity_main_tv_format2)
    TextView tvFormat2;
    @BindView(R.id.activity_main_tv_time_lived_caption)
    TextView tvLivedCaption;
    @BindView(R.id.activity_main_tv_time_left_caption)
    TextView tvWillLiveCaption;
    @BindView(R.id.adView)
    AdView adView;

    private boolean isDataAvailable = false;
    private final int seconds = 0, minutes = 1, hours = 2, days = 3, months = 4, years = 5;
    private final String formats[] = {"seconds", "minutes", "hours", "days", "months", "years"};
    private int currentFormat = seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        MyApplication.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        isDataAvailable = getIntent().getBooleanExtra(isDataPresent, false);
        if (savedInstanceState != null)
            currentFormat = savedInstanceState.getInt(format);
        initAd();
    }

    @Override
    public void onPause()
    {
        if (adView != null)
            adView.pause();

        disposeAll();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (adView != null)
            adView.resume();

        init();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(format, currentFormat);
    }

    private void initAd()
    {
        MobileAds.initialize(this, getString(R.string.adMob_app_id));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
    }

    private void init()
    {
        if (isDataAvailable)
        {
            try
            {
                final String stDate = sharedPreferences.getString(dateData, "");
                final String stDateD = sharedPreferences.getString(dateDataD, "");
                final String stTime = sharedPreferences.getString(timeData, "");

                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final Date dobDate = sdf1.parse(stDate + " " + stTime);

                final Date dodDate = sdf1.parse(stDateD + " " + stTime);

                //start the loop and show seconds
                subscriptionLived = Observable.interval(1000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .map(new Function<Long, String>()
                        {
                            @Override
                            public String apply(@NonNull Long aLong) throws Exception
                            {
                                Date currentDate = new Date();
                                return String.valueOf(
                                        NumberFormat.getIntegerInstance().format(
                                                formatData(
                                                        Math.abs(
                                                                dobDate.getTime() - currentDate.getTime()
                                                        ) / 1000
                                                )
                                        )
                                );
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>()
                        {
                            @Override
                            public void accept(String data) throws Exception
                            {
                                tvLived.setText(data);
                                tvFormat.setText(String.valueOf(formats[currentFormat]));
                                if (tvLivedCaption.getVisibility() != View.VISIBLE)
                                    tvLivedCaption.setVisibility(View.VISIBLE);
                            }
                        });


                subscriptionWillLive = Observable.interval(1000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .map(new Function<Long, String>()
                        {
                            @Override
                            public String apply(@NonNull Long aLong) throws Exception
                            {
                                Date currentDate = new Date();
                                return String.valueOf(
                                        NumberFormat.getIntegerInstance().format(
                                                formatData(
                                                        Math.abs(
                                                                dodDate.getTime() - currentDate.getTime()
                                                        ) / 1000
                                                )
                                        )
                                );
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>()
                        {
                            @Override
                            public void accept(String data) throws Exception
                            {
                                tvWillLive.setText(data);
                                tvFormat2.setText(String.valueOf(formats[currentFormat]));
                                if (tvWillLiveCaption.getVisibility() != View.VISIBLE)
                                    tvWillLiveCaption.setVisibility(View.VISIBLE);
                            }
                        });


            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        } else
        {
            //route user to add data activity
            startActivity(AddDataActivity.getIntent(this, false, fromMain));
        }
    }

    @OnClick(R.id.activity_main_root)
    public void onRootClicked()
    {
        if (currentFormat == years) currentFormat = seconds;
        else currentFormat++;
    }

    @OnClick(R.id.activity_main_settings)
    public void onTimeLivedClick()
    {
        startActivity(AddDataActivity.getIntent(this, true, fromMain));
    }

    private long formatData(long diffInSeconds)
    {
        if (currentFormat == seconds) return diffInSeconds;
        else if (currentFormat == minutes) return diffInSeconds / 60;
        else if (currentFormat == hours) return diffInSeconds / (60 * 60);
        else if (currentFormat == days) return diffInSeconds / (60 * 60 * 24);
        else if (currentFormat == months) return diffInSeconds / (60 * 60 * 24 * 30);
        else if (currentFormat == years) return diffInSeconds / (60 * 60 * 24 * 30 * 12);
        return diffInSeconds;
    }

    @Override
    protected void onDestroy()
    {
        if (adView != null) adView.destroy();
        disposeAll();
        super.onDestroy();
    }

    private void disposeAll()
    {
        if (subscriptionLived != null) subscriptionLived.dispose();
        if (subscriptionWillLive != null) subscriptionWillLive.dispose();
    }
}

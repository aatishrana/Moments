package com.aatishrana.moments;

import android.app.Application;

import com.aatishrana.moments.di.AppComponent;
import com.aatishrana.moments.di.AppModule;
import com.aatishrana.moments.di.DaggerAppComponent;
import com.aatishrana.moments.utils.CSVFile;
import com.aatishrana.moments.utils.Country;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Aatish on 6/17/2017.
 */

public class MyApplication extends Application
{
    private static AppComponent appComponent;
    private static Map<String, Country> countries;

    @Override
    public void onCreate()
    {
        super.onCreate();
        appComponent = createAppComponent();
        countries = generateCountriesData();
    }

    public static AppComponent getAppComponent()
    {
        return appComponent;
    }

    public static Map<String, Country> getCountries()
    {
        return countries;
    }

    protected AppComponent createAppComponent()
    {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .build();
    }

    private Map<String, Country> generateCountriesData()
    {
        InputStream inputStream = getResources().openRawResource(R.raw.countries);
        CSVFile csvFile = new CSVFile(inputStream);
        List<String[]> data = csvFile.read();
        Map<String, Country> countries = new HashMap<>();
        if (!data.isEmpty())
            for (String[] country : data)
            {
                if (country != null && country.length == 3)
                {
                    String countryCode = country[1];
                    String countryName = country[0];
                    String avgAge = country[2];
                    if (countryCode.length() > 0 && countryName.length() > 0 && avgAge.length() > 0)
                        countries.put(countryCode, new Country(countryName, countryCode, Double.valueOf(avgAge)));
                }
            }
        return countries;

    }
}

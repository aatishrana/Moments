package com.aatishrana.moments.data;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aatishrana.moments.MyApplication;
import com.aatishrana.moments.R;
import com.aatishrana.moments.main.MainActivity;
import com.aatishrana.moments.utils.Country;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

import static com.aatishrana.moments.utils.SharedData.dateData;
import static com.aatishrana.moments.utils.SharedData.dateDataD;
import static com.aatishrana.moments.utils.SharedData.fromMain;
import static com.aatishrana.moments.utils.SharedData.fromSplash;
import static com.aatishrana.moments.utils.SharedData.isDataPresent;
import static com.aatishrana.moments.utils.SharedData.openFrom;
import static com.aatishrana.moments.utils.SharedData.timeData;

/**
 * Created by Aatish on 6/17/2017.
 */

public class AddDataActivity extends Activity
{
    public static Intent getIntent(Context context, boolean isDataAvailable, int from)
    {
        Intent intent = new Intent(context, AddDataActivity.class);
        intent.putExtra(isDataPresent, isDataAvailable);
        intent.putExtra(openFrom, from);
        return intent;
    }


    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    TelephonyManager telephonyManager;
    @BindView(R.id.activity_data_et_date)
    EditText etDate;
    @BindView(R.id.activity_data_et_time)
    EditText etTime;

    private Subscription subscription;
    private boolean isDataAvailable = false;
    private int from = fromSplash;
    private String countryCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        MyApplication.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        ButterKnife.bind(this);
        isDataAvailable = getIntent().getBooleanExtra(isDataPresent, false);
        from = getIntent().getIntExtra(openFrom, fromSplash);
        initViews();
        countryCode = getUserCountry(telephonyManager);
    }

    @Override
    protected void onDestroy()
    {
        if (subscription != null)
            subscription.unsubscribe();
        super.onDestroy();
    }

    private void initViews()
    {
        etDate.setInputType(InputType.TYPE_NULL);
        etTime.setInputType(InputType.TYPE_NULL);
        if (isDataAvailable)
        {
            String stDate = sharedPreferences.getString(dateData, "");
            String stTime = sharedPreferences.getString(timeData, "");
            etDate.setText(stDate);
            etTime.setText(stTime);
        }
//        subscription = Observable.create(new Observable.OnSubscribe<String>()
//        {
//            private boolean ifCharIsInt(char a)
//            {
//                switch (a)
//                {
//                    case 48://0
//                    case 49://1
//                    case '2':
//                    case '3':
//                    case '4':
//                    case '5':
//                    case '6':
//                    case '7':
//                    case '8':
//                    case '9':
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//
//            @Override
//            public void call(Subscriber<? super String> subscriber)
//            {
//                Map<String, String> mapData = new HashMap<String, String>();
//                URL url;
//                HttpURLConnection urlConnection = null;
//                try
//                {
//                    String response = "";
//                    url = new URL("https://www.cia.gov/library/publications/the-world-factbook/rankorder/rawdata_2102.txt");
//                    urlConnection = (HttpURLConnection) url.openConnection();
//                    InputStream in = urlConnection.getInputStream();
//                    InputStreamReader isw = new InputStreamReader(in);
//                    int data = isw.read();
//                    char previousChar = '\r';
//                    while (data != -1)
//                    {
//                        String key = "";
//                        String value = "";
////                        String current = String.valueOf((char) data);
//                        char current = (char) data;
//                        data = isw.read();
////                        if (!(previousChar.equals(" ") && current.equals(" ")))
//                        if (!(previousChar == ' ' && current == ' '))
//                        {
//                            //if previousChar was char and current is no.
//                            //start appending in value
//                            if (!ifCharIsInt(previousChar) && ifCharIsInt(current))
//                            {
//                                value = value + current;
//                            }
//                            //if previousChar was no. and current is char
//                            //start appending in key
//                            else if (ifCharIsInt(previousChar) && ifCharIsInt(current))
//                            {
//                                key = key + current;
//                            }
//                            //if previousChar was \r and current is no.
//                            //skip
//                            else if (previousChar == '\r' && ifCharIsInt(current))
//                            {
//                                mapData.put(key, value);
//                                key = "";
//                                value = "";
//                            }
//
////                            response = response + current;
//////                            System.out.print(response);
////                            if (current == '\r')
////                                System.out.print(",");
////                            else
////                                System.out.print(current);
////                            else if (current.equals("\r"))
////                                System.out.print(".");
////                            else
////                            {
////                                response = response + current;
////                            }
//                            previousChar = current;
//                        }
//
//
//                    }
//                    subscriber.onNext(response);
//                    subscriber.onCompleted();
//
//                } catch (Exception e)
//                {
//                    e.printStackTrace();
//                } finally
//                {
//                    if (urlConnection != null)
//                    {
//                        urlConnection.disconnect();
//                    }
//                }
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
////                .map(new Func1<String, String>()
////                {
////                    @Override
////                    public String call(String s)
////                    {
////                        String key = "";
////                        String value = "";
////                        for (int i = 0; i < s.length(); i++)
////                        {
////                            char current = s.charAt(i);
//////                            System.out.println(current);
//////                            if (current == '\r\n')
//////                            {
//////                                System.out.print(',');
//////                            } else
//////                                System.out.print(',');
////                        }
////                        return null;
////                    }
////                })
//                .subscribe();
    }

    @OnClick(R.id.activity_data_et_date)
    public void onDateEtClick()
    {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener()
                {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int day)
                    {
                        int month = monthOfYear + 1;
                        String yy, mm, dd;
                        yy = String.valueOf(year);
                        mm = month < 10 ? String.valueOf("0" + month) : String.valueOf(month);
                        dd = day < 10 ? String.valueOf("0" + day) : String.valueOf(day);
                        etDate.setText(yy + "-" + mm + "-" + dd);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @OnClick(R.id.activity_data_et_time)
    public void onTimeEtClick()
    {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute)
                    {
                        String hh, mm;
                        hh = hourOfDay < 10 ? String.valueOf("0" + hourOfDay) : String.valueOf(hourOfDay);
                        mm = minute < 10 ? String.valueOf("0" + minute) : String.valueOf(minute);
                        etTime.setText(hh + ":" + mm + ":00");
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @OnClick(R.id.activity_data_btn_done)
    public void onDoneClick()
    {

        if (!(TextUtils.isEmpty(etDate.getText()) || TextUtils.isEmpty(etTime.getText())))
        {
            String stDate = etDate.getText().toString();
            String stTime = etTime.getText().toString();
            String stDateD = getDeathDate(stDate, 70d);

            if (countryCode.length() > 0)
            {
                //fetch latest data match country and go
                Map<String, Country> countries = MyApplication.getCountries();
                if (countries.containsKey(countryCode))
                {
                    //country found, get avg age find death date
                    Country country = countries.get(countryCode);
                    stDateD = getDeathDate(stDate, country.getAvgAge());
                }
            }

            sharedPreferences.edit()
                    .putString(dateData, stDate)
                    .putString(timeData, stTime)
                    .putString(dateDataD, stDateD)
                    .putBoolean(isDataPresent, true)
                    .apply();
            exit();
        } else
            Toast.makeText(this, getString(R.string.please_insert_your_data), Toast.LENGTH_SHORT).show();
    }

    private void exit()
    {
        if (from == fromSplash)
        {
            //route to main
            startActivity(MainActivity.getIntent(this, true));
            this.finish();
        } else if (from == fromMain)
        {
            //finish this activity
            this.finish();
        }
    }

    /**
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     *
     * @return country code or null
     */
    public static String getUserCountry(final TelephonyManager tm)
    {
        try
        {
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2)
            { // SIM country code is available
                return simCountry.toUpperCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA)
            { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2)
                { // network country code is available
                    return networkCountry.toUpperCase(Locale.US);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private String getDeathDate(String dob, Double avgAge)
    {
        //todo fix precision
        int addYears = avgAge.intValue();
        int dobYear = Integer.valueOf(dob.substring(0, dob.indexOf('-')));

        int newYear = addYears + dobYear;
        return newYear + "-" + dob.substring(dob.indexOf('-') + 1, dob.length());
    }
}

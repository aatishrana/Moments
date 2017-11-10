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
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
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

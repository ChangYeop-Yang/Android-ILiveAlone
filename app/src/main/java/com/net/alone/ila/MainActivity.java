package com.net.alone.ila;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.net.alone.ila.Basic.Etc;
import com.net.alone.ila.Basic.Weather;
import com.net.alone.ila.Location.LocationManagers;
import com.net.alone.ila.Philips.PhilipsHueColorManager;
import com.philips.lighting.hue.sdk.utilities.impl.Color;

import net.daum.mf.speech.api.SpeechRecognizerManager;

public class MainActivity extends BaseActivity implements View.OnClickListener
{
    /* Context */
    private Context mContext = MainActivity.this;

    /* Integer */
    private int mColor[] = {0, 0, 0};

    /* Boolean */
    private boolean mBoolean = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* setToolBar Method */
        setToolbar("홈 화면", "Philips Hue Day!");

        /* setDrawerLayout Method */
        setDrawerMethod();

        /* GPS Check */
        checkingGPSPermission(mSharedPreferences.getBoolean("GPS", false));

        /* ImageView */
        ((ImageView)findViewById(R.id.QuickPowerButton)).setOnClickListener(this);

        /* SeekBar */
        final SeekBar mSeekBar[] = {(SeekBar)findViewById(R.id.QuickREDSeekBar), (SeekBar)findViewById(R.id.QuickBLUESeekBar), (SeekBar)findViewById(R.id.QuickGREENSeekBar), (SeekBar)findViewById(R.id.QuickLUXSeekBar)};
        for(int count = 0, mLength = mSeekBar.length; count < mLength; count++) { settingSeekBar(mSeekBar[count]); }

        /* SpeechRecognizerClient */
        createSpeechRecognizer();

        /* TextView */
        final TextView mTextView = (TextView)findViewById(R.id.ConnectionTextView);
        mTextView.setText((PhilipsHueColorManager.EnabledHue() ? "연결완료" : "비연결"));
        mTextView.setTextColor(Color.rgb(139, 195, 74));
    }

    /* Checking GPS Permission Method */
    private void checkingGPSPermission(boolean mBoolean)
    {
        /* TextView */
        final TextView mWeatherTextView[] = {(TextView)findViewById(R.id.WeatherTempTextView), (TextView)findViewById(R.id.WeatherTextView), (TextView)findViewById(R.id.WeatherRainTextView), (TextView)findViewById(R.id.WeatherWindTextView)};

        if(mBoolean)
        {
            /* Setting Weather */
            final double mDouble[] = new LocationManagers(mContext).SettingWeatherLocationManager();
            new Weather(mContext, mDouble[0], mDouble[1], mWeatherTextView, (ImageView)findViewById(R.id.WeatherImageView)).execute();
        }
        else
        {
            /* AlertDialog */
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
            mBuilder.setIcon(R.drawable.ic_gps).setTitle("위치 정보 서비스 제공 동의").setMessage(getString(R.string.MainGPSComment));
            mBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    /* SharedPreferences */
                    mSharedPreferences.edit().putBoolean("GPS", true).commit();
                    Toast.makeText(mContext, "정확한 위치정보를 위해서 어플리케이션을 다시 시작해주세요.", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); System.exit(0); }
            }).show();
        }
    }

    /* SetDrawerLayout Method */
    private void setDrawerMethod()
    {
        /* DrawerLayout */
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        /* ActionBar Toggle */
        final ActionBarDrawerToggle mActionBar =  new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mActionBar);

        /* Navigation View */
        final NavigationView mNavigationView = (NavigationView)findViewById(R.id.NavigationView);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                /* Menu Name */
                final String mMenuName = menuItem.getTitle().toString();
                /* mMenuID */
                final int mMenuID = menuItem.getItemId();

                /* Toast Print */
                Toast.makeText(mContext, mMenuName, Toast.LENGTH_SHORT).show();

                /* Navigation Switch */
                switch (mMenuID) {
                    /* Alarm Activity */
                    case (R.id.Navigation_Alarm): {
                        startActivity(new Intent(mContext, AlarmActivity.class));
                        return true;
                    }
                    /* Timer Activity */
                    case (R.id.Navigation_SmartTimer): {
                        startActivity(new Intent(mContext, TimerActivity.class));
                        return true;
                    }
                    /* Light Activity */
                    case (R.id.Navigation_Light): {
                        startActivity(new Intent(mContext, LightActivity.class));
                        return true;
                    }
                    /* Life Activity */
                    case (R.id.Navigation_LifeLight): {
                        startActivity(new Intent(mContext, LifeActivity.class));
                        return true;
                    }
                    /* Kids Activity */
                    case (R.id.Navigation_Kids): {
                        startActivity(new Intent(mContext, KidsActivity.class));
                        return true;
                    }
                    /* Application Exit */
                    case (R.id.Navigation_Exit): {
                        System.exit(0);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /* Setting SeekBar Method */
    private void settingSeekBar(final SeekBar mSeekBar)
    {
        /* SeekBar Listener */
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                switch (seekBar.getId())
                {
                    case (R.id.QuickREDSeekBar): { mColor[0] = progress; break; }
                    case (R.id.QuickBLUESeekBar): { mColor[1] = progress; break; }
                    case (R.id.QuickGREENSeekBar): { mColor[2] = progress; break; }
                    case (R.id.QuickLUXSeekBar): { PhilipsHueColorManager.ChangeALLHueLUXLamp(progress); break; }
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar)
            {
                /* TextView */
                final TextView mTextView = (TextView) findViewById(R.id.QuickCodeText);
                mTextView.setText(String.format("#%02X%02X%02X", mColor[0], mColor[1], mColor[2]));
                mTextView.setTextColor(Color.rgb(mColor[0], mColor[1], mColor[2]));
                /* Philips Method */
                PhilipsHueColorManager.ChangeALLHueLampColorLamp(mColor);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId())
        {
            /* SpeechRecognizer Button */
            case (R.id.Mic) : { mSpeechRecognizerClient.startRecording(true); return false; }
            /* Setting Button */
            case (R.id.Setting) : { startActivity(new Intent(this, SettingActivity.class)); return false; }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    { getMenuInflater().inflate(R.menu.menu_main, menu); return true; }

    @Override
    public void onClick(View v)
    {
        /* Vibrate */
        Etc.Vibrate(mContext, new long[]{100, 200, 300});
        switch (v.getId())
        {
            case (R.id.QuickPowerButton) : { PhilipsHueColorManager.ChangeALLPowerHueLamp((mBoolean = mBoolean ? false : true)); break; }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        /* SpeechRecognizerManager */
        SpeechRecognizerManager.getInstance().finalizeLibrary();
    }
}

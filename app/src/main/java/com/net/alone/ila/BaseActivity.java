package com.net.alone.ila;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.net.alone.ila.Basic.SQLite;
import com.net.alone.ila.Philips.PhilipsHueColorManager;
import com.net.alone.ila.Schedule.ScheduleManager;
import com.philips.lighting.model.PHBridge;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import net.daum.mf.speech.api.SpeechRecognizeListener;
import net.daum.mf.speech.api.SpeechRecognizerClient;
import net.daum.mf.speech.api.SpeechRecognizerManager;

import java.util.ArrayList;

/**
 * Created by Mari on 2015-11-24.
 */
public class BaseActivity extends AppCompatActivity
{
    /* ToolBar */
    protected Toolbar mToolbar = null;

    /* Philips */
    protected static PHBridge mPHBridge = null;

    /* SpeechRecognizerClient */
    protected SpeechRecognizerClient mSpeechRecognizerClient = null;

    /* Integer */
    protected static final int CAMERA_CAPTURE = 100;
    protected static final int REQUEST_CODE_IMAGE= 100;
    protected static int MUSIC_MODE = 300;

    /* String */
    protected static String MUSIC_VALUE = null;

    /* SharedPreferences */
    protected SharedPreferences mSharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        /* Custom Setting Text */
        Typekit.getInstance().addNormal(Typekit.createFromAsset(this, "fonts/bmhan.ttf"));

        /* SharedPreferences */
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        MUSIC_VALUE = mSharedPreferences.getString("Music", "밝기");
        MUSIC_MODE = Integer.valueOf(mSharedPreferences.getString("MusicMode", "300")).intValue();

        super.onCreate(savedInstanceState);
    }

    /* ToolBar Method */
    protected void setToolbar(String mName1, String mName2)
    {
        /* Toolbar */
        mToolbar = (Toolbar) findViewById(R.id.ToolBar);
        mToolbar.setTitle(mName1); /* Title */
        mToolbar.setTitleTextColor(Color.WHITE); /* Title Text Color */
        mToolbar.setSubtitle(mName2); /* SubTitle */
        mToolbar.setSubtitleTextColor(Color.WHITE); /* Sub Title Text Color */
        mToolbar.setNavigationIcon(R.drawable.ic_arrow);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /* Create SpeechRecognizer */
    protected void createSpeechRecognizer()
    {
        /* ProgressDialog */
        final ProgressDialog mProgressDialog = new ProgressDialog(this);

        /* SpeechRecognizerManager */
        SpeechRecognizerManager.getInstance().initializeLibrary(this);

        /* SpeechRecognizerClient */
        mSpeechRecognizerClient = new SpeechRecognizerClient.Builder().setApiKey("c9fa40442657ba649f3fa8157919609d").setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WEB).build();
        mSpeechRecognizerClient.setSpeechRecognizeListener(new SpeechRecognizeListener()
        {
            @Override
            public void onReady()
            {
                if(mSpeechRecognizerClient == null) { return; }
                else
                {
                    /* Ui Thread */
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            /* ProgressDialog */
                            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            mProgressDialog.setMessage("명령어를 말씀해주세요.");
                            mProgressDialog.setCancelable(false);
                            mProgressDialog.show();
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s)
            {
                /* SpeechRecognizerClient Stop Recording */
                mSpeechRecognizerClient.stopRecording();

                /* Error Code Match */
                switch (i)
                {
                    /* APIKEY ERROR CODE */
                    case (SpeechRecognizerClient.ERROR_AUTH_TROUBLE) : {}
                    case (SpeechRecognizerClient.ERROR_AUTH_FAIL) : { SpeechRecognizerManager.getInstance().finalizeLibrary(); break; }
                    /* NETWORK ERROR CODE */
                    case (SpeechRecognizerClient.ERROR_NETWORK_FAIL) : {}
                    case (SpeechRecognizerClient.ERROR_NETWORK_TIMEOUT) : { break; }
                }

                /* Log */
                Log.e("Daum Speech Error", s);
            }

            @Override
            public void onResults(Bundle bundle)
            {
                /* Client */
                if(mSpeechRecognizerClient != null)
                {
                    /* Color Struct Class */
                    class CustomColorStruct
                    {
                        /* String */
                        private String mName = null;
                        /* Integer */
                        private int[] mColor = null;
                        private int mLUX = 0;

                        private CustomColorStruct(String mName, int[] mColor, int mLUX)
                        {
                            /* String */
                            this.mName = mName;
                            /* Integer */
                            this.mColor = mColor;
                            this.mLUX = mLUX;
                        }
                    }
                    /* Timer Struct Class */
                    class CustomTimerStruct
                    {
                        /* String */
                        private String mName = null;
                        /* Integer */
                        private int mTime[] = null;
                        /* boolean */
                        private boolean mPower = true;

                        private CustomTimerStruct(String mName, int[] mTime, boolean mPower)
                        {
                            /* String */
                            this.mName = mName;
                            /* Integer */
                            this.mTime = mTime;
                            /* boolean */
                            this.mPower = mPower;
                        }
                    }

                    /* Cursor */
                    final ArrayList<CustomColorStruct> mStructList = new ArrayList<CustomColorStruct>(10);
                    final ArrayList<CustomTimerStruct> mTimerList = new ArrayList<CustomTimerStruct>(10);
                    Cursor mCursor = null;

                    try
                    {
                        /* Life Database */
                        mCursor = SQLite.SelectValue(getApplicationContext(), String.format("SELECT * FROM %s", "life_db"));
                        while(mCursor.moveToNext()) { mStructList.add(new CustomColorStruct(mCursor.getString(1), new int[]{mCursor.getInt(2), mCursor.getInt(3), mCursor.getInt(4)}, mCursor.getInt(5))); }

                        /* Timer Database */
                        mCursor = SQLite.SelectValue(getApplicationContext(), String.format("SELECT * FROM %s", "timer_db"));
                        while(mCursor.moveToNext()) { mTimerList.add(new CustomTimerStruct(mCursor.getString(1), new int[]{mCursor.getInt(2), mCursor.getInt(3), mCursor.getInt(4)}, (mCursor.getInt(5) == 0 ? false : true) )); }
                    }
                    catch (SQLiteException ex) { ex.printStackTrace(); }
                    finally { mCursor.close(); }

                     /* ArrayList */
                    final ArrayList<String> mTextList = bundle.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);
                    for(int count = 0, mLength = mTextList.size(); count < mLength; count++)
                    {
                        /* Basic Word */
                        if(mTextList.get(count).equals("전원")) { PhilipsHueColorManager.ChangeALLPowerHueLamp(true); }
                        else if(mTextList.get(count).equals("종료")) { PhilipsHueColorManager.ChangeALLPowerHueLamp(false); }

                        /* Color Custom Struct Each */
                        for(CustomColorStruct mCustom : mStructList)
                        {
                            if(mTextList.get(count).equals(mCustom.mName))
                            {
                                /* Philips Hue Color Change Method */ PhilipsHueColorManager.ChangeALLHueLampColorLamp(mCustom.mColor);
                                /* Philips Hue LUX Change Method */ PhilipsHueColorManager.ChangeALLHueLUXLamp(mCustom.mLUX);
                            }
                        }

                        /* Timer Custom Struct Each */
                        for(CustomTimerStruct mCustom : mTimerList)
                        {
                            if(mTextList.get(count).equals(String.format("%s타이머", mCustom.mName)))
                            { ScheduleManager.CreateSmartTimer(getApplicationContext(), mCustom.mName, mCustom.mTime[0], mCustom.mTime[1], mCustom.mTime[2], ((int)Math.random()*100), mCustom.mPower); }
                        }
                    }
                }
            }

            @Override public void onBeginningOfSpeech() {}
            @Override public void onEndOfSpeech() {}
            @Override public void onPartialResult(String s) {}
            @Override public void onAudioLevel(float v) {}
            @Override public void onFinished() { mProgressDialog.dismiss(); mSpeechRecognizerClient.stopRecording(); }
        });
    }

    /* Typekit */
    @Override protected void attachBaseContext(Context newBase) { super.attachBaseContext(TypekitContextWrapper.wrap(newBase)); }
}

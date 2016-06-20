package com.net.alone.ila;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.net.alone.ila.Basic.Adapter.LifeAdapter;
import com.net.alone.ila.Basic.Etc;
import com.net.alone.ila.Basic.SQLite;
import com.net.alone.ila.Philips.PhilipsHueColorManager;
import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mari on 2015-12-08.
 */
public class LifeActivity extends BaseActivity implements SensorEventListener, View.OnClickListener
{
    /* Context */
    private Context mContext = null;

    /* SensorManager */
    private static final int SHAKE_THRESHOLD = 800;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    /* Float */
    private float mXYZ[] = {0,0,0};
    private float mLastXYZ[] = {0,0,0};
    private float mSpeed = 0;

    /* Long */
    private long mLastTime = 0;

    /* int */
    private int mPHLightSize = 0;
    private int scrolledDistance  = 0;
    private int scrollItemCount = 0;
    private int mColorArray[] = {0, 0, 0};

    /* Boolean */
    private boolean controlsVisible  = true;

    /* PHLite */
    private List<PHLight> mPHLight = null;

    /* Timer */
    private Timer mTimer = null;

    /* Boolean */
    private boolean mCheckMode[] = {true, true};

    /* SoundMeter */
    private Etc.SoundMeter mSoundMeter = null;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life);

        /* Context */
        mContext = LifeActivity.this;

        /* Philips PHBridge */
        mPHLight = mPHBridge.getResourceCache().getAllLights();
        mPHLightSize = mPHLight.size();

        /* Life ToolBar Method */
        setToolbar("생활", null);

        /* TabBar Method */
        setTabLayout(new String[]{"집중", "휴식", "독서", "활력"}, new int[]{R.drawable.ic_study, R.drawable.ic_rest, R.drawable.ic_book, R.drawable.ic_work});

        /* Floating Button */
        final FloatingActionButton mFloatingActionButton = (FloatingActionButton)findViewById(R.id.LifeSaveFloating);
        mFloatingActionButton.setOnClickListener(this);

        /* Recycler Method */
        setRecyclerView((RecyclerView) findViewById(R.id.LifeRecyclerView), mFloatingActionButton);
    }

    /* TabLayout */
    private void setTabLayout(String[] mString, int[] mInt)
    {
        /* TabLayout */
        final TabLayout mTabLayout = (TabLayout) findViewById(R.id.TabLayout);
        mTabLayout.setVisibility(View.VISIBLE);

        for(int count = 0, mLength = mString.length; count < mLength; count++)
        {
            /* TextView */
            final TextView mCustomTabTextView = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tap, null);
            mCustomTabTextView.setText(mString[count]); /* Tab Text Setting */
            mCustomTabTextView.setCompoundDrawablesWithIntrinsicBounds(0, mInt[count], 0, 0); /* Tab Icon Setting */
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(mCustomTabTextView).setText(mString[count]).setTag(count));
        }

        /* TabLayout Listener */
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                /* Toast */
                Toast.makeText(mContext, tab.getText(), Toast.LENGTH_SHORT).show();
                switch ((int) tab.getTag()) {
                    case (0): {
                        PhilipsHueColorManager.ChangeALLHueLampColorLamp(new int[]{255, 100, 0});
                        break;
                    }
                    case (1): {
                        PhilipsHueColorManager.ChangeALLHueLampColorLamp(new int[]{0, 125, 255});
                        break;
                    }
                    case (2): {
                        PhilipsHueColorManager.ChangeALLHueLampColorLamp(new int[]{255, 200, 0});
                        break;
                    }
                    case (3): {
                        PhilipsHueColorManager.ChangeALLHueLampColorLamp(new int[]{255, 228, 0});
                        break;
                    }
                }
                /* Philips LUX Change Method */
                PhilipsHueColorManager.ChangeALLHueLUXLamp(110);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /* Setting RecyclerView Method */
    private void setRecyclerView(RecyclerView mRecyclerView, final View mView)
    {
        /* RecyclerView Init */
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /* ArrayList */
        final ArrayList<LifeAdapter.Life> mArrayList = new ArrayList<LifeAdapter.Life>(10);

        /* Cursor */
        final Cursor mCursor = SQLite.SelectValue(mContext, String.format("SELECT * FROM %s", "life_db"));
        try
        {
            while(mCursor.moveToNext())
            { mArrayList.add(new LifeAdapter.Life(mCursor.getString(1), new int[]{mCursor.getInt(2), mCursor.getInt(3), mCursor.getInt(4)}, mCursor.getInt(5), mCursor.getInt(0))); }

            /* ToolBar */
            mToolbar.setSubtitle(String.format("%d 개", scrollItemCount = mCursor.getCount()));

            /* LifeAdapter */
            mRecyclerView.setAdapter(new LifeAdapter(mContext, mArrayList));
        }
        catch (SQLiteException ex) { ex.printStackTrace(); }
        finally { mCursor.close(); }

        /* Listener */
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                /* Scroll Bottom Move */
                if (scrolledDistance > scrollItemCount && controlsVisible) {
                    mView.setVisibility(View.GONE);
                    controlsVisible = false;
                    scrolledDistance = 0;
                }
                /* Scroll Top Move */
                else if (scrolledDistance < -scrollItemCount && !controlsVisible) {
                    mView.setVisibility(View.VISIBLE);
                    controlsVisible = true;
                    scrolledDistance = 0;
                }
                if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                    scrolledDistance += dy;
                }
            }
        });
    }

    /* Setting MusicMode Method */
    private void setMusicMode(boolean mSwitch)
    {
        if(mSwitch)
        {
            /* SoundMeter */
            mSoundMeter = new Etc.SoundMeter(mContext);

            /* SoundMeter Start Method */
            mSoundMeter.start();

            /* Timer */
            if(mTimer != null) { mTimer.cancel(); mTimer = null; }
            else
            {
                /* Random */
                final Random mRandom = new Random();

                /* Timer Schedule */
                mTimer = new Timer();
                mTimer.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        final int mTemp = ((int)mSoundMeter.getAmplitude()) / 200;
                        mColorArray[(mRandom.nextInt(mPHLightSize))] = mTemp;
                        switch (MUSIC_VALUE)
                        {
                            case ("밝기") : { PhilipsHueColorManager.ChangeLUXHueLamp(mPHLight.get(mRandom.nextInt(mPHLightSize)), mTemp); break; }
                            case ("색상") : { PhilipsHueColorManager.ChangeColorHueLamp(mPHLight.get(mRandom.nextInt(mPHLightSize)), mColorArray); break; }
                            case ("색상 + 밝기") : { PhilipsHueColorManager.ChangeHueColorLUXLamp(mPHLight.get(mRandom.nextInt(mPHLightSize)), mColorArray, mTemp); break; }
                        }

                        /* Value Reset Schedule */
                        new Timer().schedule(new TimerTask()
                        { @Override public void run() { mColorArray = new int[3]; } }, 0, 5000);
                    }
                }, 0, MUSIC_MODE);
                mCheckMode[0] = false;
            }
        }
        else
        {
            /* SoundMeter End Method */
            mSoundMeter.stop();

            /* Timer */
            mTimer.cancel(); mTimer = null;
            mCheckMode[0] = true;
        }

        /* Toast */
        Toast.makeText(mContext, String.format("Music Mode %s", (mSwitch) ? "활성화" : "비활성화"), Toast.LENGTH_SHORT).show();
    }

    /* Setting ShakeMode Method */
    private void setShakeMode(boolean mSwitch)
    {
        /* SensorManager */
        final SensorManager mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        final Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(mSwitch) { mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME); mCheckMode[1] = false; }
        else { mSensorManager.unregisterListener(this); mCheckMode[1] = true; }

        /* Toast */
        Toast.makeText(mContext, String.format("Shake Mode %s", (mSwitch ? "활성화" : "비활성화")), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case (R.id.LifeSaveFloating) : {  startActivity(new Intent(this, LifeSaveActivity.class)); break; }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    { getMenuInflater().inflate(R.menu.menu_life, menu); return true; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId())
        {
            case (R.id.LifeSound) : { setMusicMode(mCheckMode[0]); return false; }
            case (R.id.LifeShake) : { setShakeMode(mCheckMode[1]); return false; }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            /* Time Value */
            final long mCurrentTime = System.currentTimeMillis();
            final long mGabTime = mCurrentTime - mLastTime;

            if(mGabTime > 100)
            {
                mLastTime = mCurrentTime;
                /* Sensor Value */
                mXYZ[0] = event.values[SensorManager.DATA_X];
                mXYZ[1] = event.values[SensorManager.DATA_Y];
                mXYZ[2] = event.values[SensorManager.DATA_Z];
                /* Speed */
                mSpeed = Math.abs(mXYZ[0] + mXYZ[1] + mXYZ[2] - mLastXYZ[0] - mLastXYZ[1] - mLastXYZ[2]) / mGabTime * 10000;

                /* Shake Event Start */
                if(mSpeed > SHAKE_THRESHOLD)
                {
                    /* Random */
                    final Random mRandom = new Random();

                    /* Philips Color Method */
                    PhilipsHueColorManager.ChangeColorHueLamp(mPHLight.get(mRandom.nextInt(mPHLightSize)), new int[]{mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255)});
                    /* Philips LUX Method */
                    PhilipsHueColorManager.ChangeLUXHueLamp(mPHLight.get(mRandom.nextInt(mPHLightSize)), mRandom.nextInt(255));
                }

                /* Last Sensor Value */
                mLastXYZ[0] = event.values[DATA_X];
                mLastXYZ[1] = event.values[DATA_Y];
                mLastXYZ[2] = event.values[DATA_Z];
            }

        }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onRestart()
    {
        super.onRestart();
        /* RecyclerView */
        setRecyclerView((RecyclerView) findViewById(R.id.LifeRecyclerView), (FloatingActionButton)findViewById(R.id.LifeSaveFloating));
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(mTimer != null && mSoundMeter != null) { mTimer.cancel(); mSoundMeter.stop(); }
    }
}

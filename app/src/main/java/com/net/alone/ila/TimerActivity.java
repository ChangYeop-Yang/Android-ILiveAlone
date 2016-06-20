package com.net.alone.ila;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.net.alone.ila.Basic.Adapter.TimerAdapter;
import com.net.alone.ila.Basic.Etc;
import com.net.alone.ila.Basic.SQLite;
import com.net.alone.ila.Schedule.ScheduleManager;

import java.util.ArrayList;

/**
 * Created by Mari on 2015-11-24.
 */
public class TimerActivity extends BaseActivity implements View.OnClickListener
{
    /* Context */
    private Context mContext = TimerActivity.this;

    /* Time */
    private int mTime[] = {0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        /* setToolBar Method */
        setToolbar("스마트타이머 화면", null);

        /* setRecyclerView Method */
        setRecyclerView((RecyclerView) findViewById(R.id.TimerRecyclerView));

        /* setNumberPicker Method */
        final NumberPicker mNumberPicker[] = {(NumberPicker)findViewById(R.id.TimerHourPicker), (NumberPicker)findViewById(R.id.TimerMinutePicker), (NumberPicker)findViewById(R.id.TimerSecondsPicker)};
        for(int count = 0, mLength = mNumberPicker.length; count < mLength; count++) { setNumberPick(mNumberPicker[count]); }

        /* Button */
        final Button mButton[] = {(Button)findViewById(R.id.TimerStartButton), (Button)findViewById(R.id.TimerResetButton), (Button)findViewById(R.id.TimerCreateButton)};
        for (int count = 0, mLength = mButton.length; count < mLength; count++) { mButton[count].setOnClickListener(this); }
    }

    /* Setting RecyclerView Method */
    private void setRecyclerView(final RecyclerView mRecyclerView)
    {
        /* RecyclerView Init */
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /* ArrayList */
        final ArrayList<TimerAdapter.Timer> mArrayList = new ArrayList<TimerAdapter.Timer>(10);

        /* Cursor */
        final Cursor mCursor = SQLite.SelectValue(mContext, String.format("SELECT * FROM %s", "timer_db"));
        try
        {
            while(mCursor.moveToNext())
            { mArrayList.add(new TimerAdapter.Timer(mCursor.getString(1), new int[]{mCursor.getInt(2), mCursor.getInt(3), mCursor.getInt(4)}, mCursor.getInt(6), mCursor.getInt(0), (mCursor.getInt(5) == 0 ? false : true))); }

            /* ToolBar */
            mToolbar.setSubtitle(String.format("%d 개", mCursor.getCount()));

            /* LifeAdapter */
            mRecyclerView.setAdapter(new TimerAdapter(mContext, mArrayList));
        }
        catch (SQLiteException ex) { ex.printStackTrace(); }
        finally { mCursor.close(); }

         /* DisplayMetrics */
        final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        /* Height */
        int mHeight = (mDisplayMetrics.heightPixels * 90) / 100;
        mRecyclerView.getLayoutParams().height = mHeight;
    }

    /* Setting NumberPick Method */
    private void setNumberPick(NumberPicker mNumberPicker)
    {
        /* NumberPicker Basic Value */
        switch (mNumberPicker.getId())
        {
            case (R.id.TimerHourPicker) : { mNumberPicker.setMaxValue(99); break; }
            case (R.id.TimerMinutePicker) : { mNumberPicker.setMaxValue(59); break; }
            case (R.id.TimerSecondsPicker) : { mNumberPicker.setMaxValue(59); break; }
        } mNumberPicker.setMinValue(0);

        /* NumberPicker Listener */
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                /* Vibrate */
                Etc.Vibrate(mContext, new long[]{100});

                /* Check Zero */
                if(mTime[0] == 0 && mTime[1] == 0 && mTime[2] == 0) { initView(true); } else { initView(false); }

                switch (picker.getId())
                {
                    case (R.id.TimerHourPicker) : { mTime[0] = newVal; break; }
                    case (R.id.TimerMinutePicker) : { mTime[1] = newVal; break; }
                    case (R.id.TimerSecondsPicker) : { mTime[2] = newVal; break; }
                }
            }
        });
    }

    /* Init View Method */
    private void initView(boolean mBoolean)
    {
        /* Button */
        final Button mButton[] = {(Button)findViewById(R.id.TimerStartButton), (Button)findViewById(R.id.TimerResetButton), (Button)findViewById(R.id.TimerCreateButton)};

        if(mBoolean)
        {
            for(int count = 0, mLength = mButton.length; count < mLength; count++) { mButton[count].setEnabled(false); }

            /* NumberPicker */
            final NumberPicker mNumberPicker[] = {(NumberPicker)findViewById(R.id.TimerHourPicker), (NumberPicker)findViewById(R.id.TimerMinutePicker), (NumberPicker)findViewById(R.id.TimerSecondsPicker)};
            for(int count = 0, mLength = mNumberPicker.length; count < mLength; count++) { mNumberPicker[count].setValue(0); }

            /* EditText */ ((AppCompatEditText)findViewById(R.id.TimerNameEditText)).setText(null);
        }
        else { for(int count = 0, mLength = mButton.length; count < mLength; count++) { mButton[count].setEnabled(true); } }
    }

    @Override
    public void onClick(View v)
    {
        /* Vibrate */
        Etc.Vibrate(mContext, new long[]{100, 200, 300});

        switch (v.getId())
        {
            /* Timer Start Button */
            case (R.id.TimerStartButton) :
            {
                /* Schedule Method */
                ScheduleManager.CreateSmartTimer(mContext, "Quick Timer", mTime[0], mTime[1], mTime[2], 7777, ((SwitchCompat) findViewById(R.id.TimerPowerSwitch)).isChecked());
                /* View Init Method */ initView(true); break;
            }
            /* Timer Reset Button */
            case (R.id.TimerResetButton) : { initView(true); break; }
            /* Timer Create Button */
            case (R.id.TimerCreateButton) :
            {
                /* ContentValue */
                final ContentValues mContentValues = new ContentValues();
                /* Name */
                final String mString = ((AppCompatEditText)findViewById(R.id.TimerNameEditText)).getText().toString();
                /* Name */ mContentValues.put("title", (mString.equals("") ? "사용자 정의 타이머": mString) );
                /* Timer */ mContentValues.put("hour", mTime[0]); mContentValues.put("minute", mTime[1]); mContentValues.put("seconds", mTime[2]);
                /* Image */ mContentValues.put("img", R.drawable.ic_alarm_64);
                /* power */ mContentValues.put("power", ((SwitchCompat) findViewById(R.id.TimerPowerSwitch)).isChecked());

                /* SQLite Insert Method */ SQLite.InsertValue(mContext, "timer_db", mContentValues);

                /* View Init Method */ initView(true); break;
            }
        }
    }
}

package com.net.alone.ila;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.net.alone.ila.Basic.Etc;
import com.net.alone.ila.Basic.SQLite;
import com.net.alone.ila.Schedule.ScheduleManager;

/**
 * Created by Mari on 2015-12-04.
 */
public class AlarmSaveActivity extends BaseActivity
{
    /* Integer */
    private int mTime[] = {0, 0};

    /* Integer */
    private int mBoolean[] = {0, 0, 0, 0, 0, 0, 0};
    private int mColorInteger[] = {255, 255, 255};

    /* Context */
    private Context mContext = null;

    /* RadioButton */
    private RadioButton mRadioButton[] = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_save);

        /* ToolBar */
        setToolbar("알람 만들기", null);

        /* Context */
        mContext = AlarmSaveActivity.this;

        /* NumberPicker */
        final NumberPicker mNumberPicker[] = { (NumberPicker)findViewById(R.id.AlarmDialogHour), (NumberPicker)findViewById(R.id.AlarmDialogMinute) };
        for(int count = 0, mLength = mNumberPicker.length; count < mLength; count++) { setNumberPicker(mNumberPicker[count]); }

        /* Button */
        final Button mButton[] = {(Button)findViewById(R.id.AlarmItemMonth), (Button)findViewById(R.id.AlarmItemTuesday), (Button)findViewById(R.id.AlarmItemWednesday), (Button)findViewById(R.id.AlarmItemThursday),
                (Button)findViewById(R.id.AlarmItemFriday), (Button)findViewById(R.id.AlarmItemSaturday), (Button)findViewById(R.id.AlarmItemSunday)};
        for(int count = 0, mLength = mButton.length; count < mLength; count++) { setButton(mButton[count]); }

        /* RadioButton */
        mRadioButton = new RadioButton[]{(RadioButton)findViewById(R.id.TimerDialogRed), (RadioButton)findViewById(R.id.TimerDialogBlue), (RadioButton)findViewById(R.id.TimerDialogGreen), (RadioButton)findViewById(R.id.TimerDialogWhite),
                (RadioButton)findViewById(R.id.TimerDialogPink), (RadioButton)findViewById(R.id.TimerDialogPurple), (RadioButton)findViewById(R.id.TimerDialogOrange), (RadioButton)findViewById(R.id.TimerDialogBrown), (RadioButton)findViewById(R.id.TimerDialogYellow)};
        for(int count = 0, mLength = mRadioButton.length; count < mLength; count++) { setRadioButton(mRadioButton[count]); }

        /* ImageButton */
        setImageButton((ImageButton) findViewById(R.id.AlarmDialogRecyclerButton));
    }

    /* Setting NumberPicker Method */
    private void setNumberPicker(NumberPicker mNumberPicker)
    {
        /* NumberPicker Value Setting */
        switch (mNumberPicker.getId())
        {
            /* Hour NumberPicker */
            case (R.id.AlarmDialogHour) :
            {
                mNumberPicker.setMaxValue(24);
                mNumberPicker.setMinValue(0); break;
            }
            /* Minute NumberPicker */
            case (R.id.AlarmDialogMinute) :
            {
                mNumberPicker.setMaxValue(59);
                mNumberPicker.setMinValue(0); break;
            }
        }

        /* NumberPick */
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                /* Vibrate */
                Etc.Vibrate(mContext, new long[]{100});

                switch (picker.getId()) {
                    /* Hour NumberPicker */
                    case (R.id.AlarmDialogHour): {
                        mTime[0] = newVal;
                        break;
                    }
                    /* Minute NumberPicker */
                    case (R.id.AlarmDialogMinute): {
                        mTime[1] = newVal;
                        break;
                    }
                }
            }
        });
    }

    /* Setting Button Method */
    private void setButton(Button mButton)
    {
        /* Listener */
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /* Vibrate */
                Etc.Vibrate(mContext, new long[]{100, 200, 300});

                int mCheck = 0;

                /* Button Color Matching */
                if (((Button) v).getCurrentTextColor() == Color.rgb(114, 114, 114)) { ((Button) v).setTextColor(Color.rgb(139, 195, 74)); mCheck = 1; }
                else { ((Button) v).setTextColor(Color.rgb(114, 114, 114)); mCheck = 0; }

                switch (v.getId())
                {
                    /* Sunday */
                    case (R.id.AlarmItemSunday): { mBoolean[0] = mCheck; break; }
                    /* Month */
                    case (R.id.AlarmItemMonth): { mBoolean[1] = mCheck; break; }
                    /* Tuesday */
                    case (R.id.AlarmItemTuesday): { mBoolean[2] = mCheck; break; }
                    /* Wednesday */
                    case (R.id.AlarmItemWednesday): { mBoolean[3] = mCheck; break; }
                    /* Thursday */
                    case (R.id.AlarmItemThursday): { mBoolean[4] = mCheck; break; }
                    /* Friday */
                    case (R.id.AlarmItemFriday): { mBoolean[5] = mCheck; break; }
                    /* Saturday */
                    case (R.id.AlarmItemSaturday): { mBoolean[6] = mCheck; break; }
                }
            }
        });
    }

    /* Setting ImageButton Method */
    private void setImageButton(ImageButton mImageButton)
    {
        mImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /* NumberPicker Recycler */
                ((NumberPicker)findViewById(R.id.AlarmDialogHour)).setValue(0);
                ((NumberPicker)findViewById(R.id.AlarmDialogMinute)).setValue(0);
            }
        });
    }

    /* Setting RadioButton Method */
    private void setRadioButton(final RadioButton mRadioButtons)
    {
        /* Listener */
        mRadioButtons.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                for (int count = 0, mLength = mRadioButton.length; count < mLength; count++)
                { if (v.getId() != mRadioButton[count].getId()) { mRadioButton[count].setChecked(false); } }

                /* Vibrate */
                Etc.Vibrate(mContext, new long[]{100, 200, 300});

                switch (v.getId())
                {
                    /* RED */ case (R.id.TimerDialogRed): { mColorInteger = new int[]{255, 0, 0}; break; }
                    /* BLUE */ case (R.id.TimerDialogBlue): { mColorInteger = new int[]{0, 0, 255}; break; }
                    /* GREEN */ case (R.id.TimerDialogGreen): { mColorInteger = new int[]{0, 255, 0}; break; }
                    /* WHITE */ case (R.id.TimerDialogWhite): { mColorInteger = new int[]{255, 255, 255}; break; }
                    /* BROWN */ case (R.id.TimerDialogBrown): { mColorInteger = new int[]{75, 7, 0}; break; }
                    /* Pink */ case (R.id.TimerDialogPink): { mColorInteger = new int[]{255, 0, 221}; break; }
                    /* Purple */ case (R.id.TimerDialogPurple): { mColorInteger = new int[]{95, 0, 255}; break; }
                    /* Orange */ case (R.id.TimerDialogOrange): { mColorInteger = new int[]{255, 187, 0}; break; }
                    /* Yellow */ case (R.id.TimerDialogYellow): { mColorInteger = new int[]{255, 228, 0}; break; }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    { getMenuInflater().inflate(R.menu.menu_save, menu); return true; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        /* ActionBar Item */
        switch (item.getItemId())
        {
            /* Alarm Save Button */
            case (R.id.CustomSave) :
            {
                if(mTime[0] != 0 || mTime[1] != 0)
                {
                    /* Integer */
                    final int mID = ((int)Math.random()*7777);

                    /* ContentValue */
                    final ContentValues mContentValues = new ContentValues();

                    /* Alarm ID */
                    mContentValues.put("_id", mID);

                    /* Alarm Name */
                    final String mName = ((AppCompatEditText) findViewById(R.id.AlarmNameEdit)).getText().toString();
                    mContentValues.put("name", (mName.equals("") ? "이름 없음" : mName));

                    /* Alarm Time */
                    mContentValues.put("hour", mTime[0]); mContentValues.put("minute", mTime[1]);

                    /* Alarm Repeat */
                    boolean mCheckBoolean = false;
                    for (int count = 0, mLength = mBoolean.length; count < mLength; count++) {if (mBoolean[count] == 1) { mCheckBoolean = true; break; } }
                    mContentValues.put("repeat", mCheckBoolean);
                    mContentValues.put("A", mBoolean[0]); mContentValues.put("B", mBoolean[1]);
                    mContentValues.put("C", mBoolean[2]); mContentValues.put("D", mBoolean[3]);
                    mContentValues.put("E", mBoolean[4]); mContentValues.put("F", mBoolean[5]);
                    mContentValues.put("G", mBoolean[6]);

                    /* Alarm Activity */
                    mContentValues.put("activity", true);

                    /* Alarm Color */
                    mContentValues.put("CR", mColorInteger[0]); mContentValues.put("CG", mColorInteger[1]); mContentValues.put("CB", mColorInteger[2]);

                    /* Alarm Power */
                    final boolean mCheck = ((SwitchCompat)findViewById(R.id.AlarmDialogPower)).isChecked();
                    mContentValues.put("power", mCheck);

                    /* SQLite Insert Method */
                    SQLite.InsertValue(mContext, "alarm_db", mContentValues);

                    /* Schedule Create Method */
                    if(mCheckBoolean) { ScheduleManager.CreateAlarm(mContext, mName, mID, mTime[0], mTime[1], mBoolean, mColorInteger, mCheck); }
                    else { ScheduleManager.CreateAlarm(mContext, mName, mID, mTime[0], mTime[1], null, mColorInteger, mCheck); }

                    finish();
                }  else { Toast.makeText(getApplicationContext(), "알람 시간을 지정해주세요.", Toast.LENGTH_SHORT).show(); }

                return false;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}

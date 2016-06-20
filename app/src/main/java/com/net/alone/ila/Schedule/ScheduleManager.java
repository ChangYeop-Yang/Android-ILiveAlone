package com.net.alone.ila.Schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by Mari on 2015-12-02.
 */
public class ScheduleManager
{
    /* SmartTimer Create Method */
    public static void CreateSmartTimer(Context mContext, String mName, int mHour, int mMinute, int mSeconds, final int mID, boolean mSwitch)
    {
        /* long */
        final long second = ((mHour * 3600) + (mMinute * 60) + mSeconds) * 1000;

        /* AlarmManager */
        final AlarmManager mAlarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);

        /* Random */
        final Random mRandom = new Random();

        /* Intent */
        final Intent mIntent = new Intent(mContext, TimerBroadCast.class).putExtra("Color", new int[]{mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255)}).putExtra("Power", mSwitch);

        /* PendingIntent */
        final PendingIntent mPendingIntent = PendingIntent.getBroadcast(mContext, mID, mIntent, PendingIntent.FLAG_ONE_SHOT);

        /* Alarm Create */
        mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + second, mPendingIntent);

        /* Toast */
        Toast.makeText(mContext, String.format("%s 타이머 실행", mName), Toast.LENGTH_SHORT).show();
    }

    /* Alarm Create Method */
    public static void CreateAlarm(Context mContext, String mName, final int mId, int mHour, int mMinute, int[] mRepeat, int[] mColor, boolean mSwitch)
    {
        /* AlarmManager */
        final AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        if(mRepeat == null)
        {
            /* Intent */
            final Intent mIntent = new Intent(mContext, AlarmBroadCast.class).putExtra("Menu", false).putExtra("Color", mColor).putExtra("Power", mSwitch).putExtra("Name", mName).putExtra("Repeat", mRepeat);

            /* PendingIntent */
            final PendingIntent mPendingIntent = PendingIntent.getBroadcast(mContext, mId, mIntent, PendingIntent.FLAG_ONE_SHOT);

            mAlarmManager.set(mAlarmManager.RTC_WAKEUP, setTriggerTime(mHour, mMinute), mPendingIntent);
        }
        /* setRepeating */
        else
        {
            /* PendingIntent */
            PendingIntent mPendingIntent = PendingIntent.getBroadcast(mContext, mId, new Intent(mContext, AlarmBroadCast.class), PendingIntent.FLAG_NO_CREATE);

            /* Intent */
            final Intent mIntent = new Intent(mContext, AlarmBroadCast.class).putExtra("Menu", false).putExtra("Color", mColor).putExtra("Power", mSwitch).putExtra("Name", mName).putExtra("Repeat", mRepeat);

            /* 저장 된 알람이 존재하지 않을 경우 */
            if(mPendingIntent == null) { mPendingIntent = PendingIntent.getBroadcast(mContext, mId, mIntent, 0); }
            /* 저장 된 알람이 존재 하는 경우 */ else { mPendingIntent = PendingIntent.getBroadcast(mContext, mId, mIntent, PendingIntent.FLAG_UPDATE_CURRENT); }

            mAlarmManager.setRepeating(mAlarmManager.RTC_WAKEUP, setTriggerTime(mHour, mMinute), AlarmManager.INTERVAL_DAY, mPendingIntent);
        }
    }

    /* Date Alarm Create Method */
    public static void  CreateDateAlarm(Context mContext, String mName, final long mTime,final int[] mRepeat, final int mID)
    {
        /* AlarmManager */
        final AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        /* Integer */
        final int mColor[] = new int[]{((int)Math.random()*255), ((int)Math.random()*255), ((int)Math.random()*255)};

        /* Intent */
        final Intent mIntent = new Intent(mContext, AlarmBroadCast.class).putExtra("Menu", false).putExtra("Color", mColor).putExtra("Power", true).putExtra("Name", mName).putExtra("Repeat", mRepeat);

        /* PendingIntent */
        final PendingIntent mPendingIntent = PendingIntent.getBroadcast(mContext, mID, mIntent, PendingIntent.FLAG_ONE_SHOT);

        mAlarmManager.set(mAlarmManager.RTC_WAKEUP, mTime, mPendingIntent);
    }

    /* Weather Create Method */
    public static void WeatherAlarm(Context mContext, int mID, int mHour, int mMinute)
    {
        /* AlarmManager */
        final AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        /* Alarm Checking */
        PendingIntent mPendingIntentIntent = PendingIntent.getBroadcast(mContext, mID, new Intent(mContext, AlarmBroadCast.class), PendingIntent.FLAG_NO_CREATE);

        /* Intent */
        final Intent mIntent = new Intent(mContext, AlarmBroadCast.class).putExtra("Menu", true);

        /* UnSet Weather Alarm */
        if(mPendingIntentIntent == null) /* 저장 된 알람이 존재하지 않을 경우 */
        { mPendingIntentIntent = PendingIntent.getBroadcast(mContext, mID, mIntent, 0); }
        else /* 저장 된 알람이 존재 하는 경우 */
        { mPendingIntentIntent = PendingIntent.getBroadcast(mContext, mID, mIntent, PendingIntent.FLAG_UPDATE_CURRENT); }

        /* Repeating */
        mAlarmManager.setRepeating(mAlarmManager.RTC_WAKEUP, setTriggerTime(mHour, mMinute) , AlarmManager.INTERVAL_DAY, mPendingIntentIntent);
    }

    /* Alarm Delete Method */
    public static void DeleteAlarmManager(Context mContext, int mID)
    {
        /* AlarmManager */
        final AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        /* PendingIntent */
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(mContext, mID, new Intent(mContext, AlarmBroadCast.class), PendingIntent.FLAG_NO_CREATE);

        if(mPendingIntent != null) { /* Cancel AlarmManager */ mAlarmManager.cancel(mPendingIntent); mPendingIntent.cancel(); }
    }

    /* Setting Trigger Time Method */
    private static long setTriggerTime(int mHour, int mMinute)
    {
        /* Calendar */
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        /* Long */
        final long mCurrentTime = System.currentTimeMillis();
        final long mSystemTime = mCalendar.getTimeInMillis();
        long mTriggerTime = mSystemTime;

        if(mCurrentTime > mSystemTime) { mTriggerTime += 1000 * 60 * 60 * 24; }

        return  mTriggerTime;
    }
}

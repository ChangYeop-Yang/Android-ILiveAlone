package com.net.alone.ila.Basic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.net.alone.ila.Calendars.Calendars;
import com.net.alone.ila.Schedule.ScheduleManager;

/**
 * Created by Mari on 2015-12-07.
 */
public class BootService extends Service
{
    @Override
    public void onCreate()
    {
        /* SharedPreferences */
        final SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        /* Booting AlarmManager Method */
        bootAlarmManager(getBaseContext());

        /* Booting Import Calendar Method */
        if(mSharedPreferences.getBoolean("Calendar", false)) { Calendars.ImportCalendars(getBaseContext(), true); }

        /* Booting Weather */
        if(mSharedPreferences.getBoolean("Weather", false)) { ScheduleManager.WeatherAlarm(getBaseContext(), 200, mSharedPreferences.getInt("WeatherHour", 7), mSharedPreferences.getInt("WeatherMinute", 30)); }
    }

    @Override
    public void onStart(Intent intent, int startId) { super.onStart(intent, startId); }

    @Override
    public void onDestroy() { super.onDestroy(); }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    /* Booting AlarmManager Method */
    private void bootAlarmManager(Context mContext)
    {
        /* Cursor */
        final Cursor mCursor = SQLite.SelectValue(mContext, String.format("SELECT * FROM %s", "alarm_db"));

        while(mCursor.moveToNext())
        {
            /* Activity Alarm */
            if(mCursor.getInt(5) == 1)
            {
                /* Integer */
                int mSchedule[] = new int[]{mCursor.getInt(6), mCursor.getInt(7), mCursor.getInt(8), mCursor.getInt(9), mCursor.getInt(10), mCursor.getInt(11), mCursor.getInt(12)};
                int mColor[] = new int[]{mCursor.getInt(13), mCursor.getInt(14), mCursor.getInt(15)};

                /* Boolean */
                boolean mBoolean = mCursor.getInt(16) == 0 ? false : true;

                /* ScheduleManager Alarm Method */
                ScheduleManager.CreateAlarm(mContext, mCursor.getString(1), mCursor.getInt(0), mCursor.getInt(2), mCursor.getInt(3), mSchedule, mColor, mBoolean);
            }
        }
    }
}

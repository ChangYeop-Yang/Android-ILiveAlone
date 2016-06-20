package com.net.alone.ila.Calendars;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;

import com.net.alone.ila.Schedule.ScheduleManager;

import java.util.Calendar;

/**
 * Created by Mari on 2015-12-17.
 */
public class Calendars
{
    /* Import Calendar Method */
    public static void ImportCalendars(Context mContext, boolean mSwitch)
    {
        /* Cursor */
        Cursor mCursor = null;
        try
        {
            /* Calendar */
            final Calendar mCalendar = Calendar.getInstance();

            /* String */
            final String mQuery[] = {"_id", "title", "dtend"};
            mCursor = mContext.getContentResolver().query(Uri.parse("content://com.android.calendar/events"), mQuery, null, null, null);

            while (mCursor.moveToNext())
            {
                /* Time Millis */
                mCalendar.setTimeInMillis(mCursor.getLong(2));
                if(mCalendar.getTimeInMillis() - System.currentTimeMillis() > 0 && mSwitch) { ScheduleManager.CreateDateAlarm(mContext, mCursor.getString(1), mCalendar.getTimeInMillis(), null, mCursor.getInt(0)); }
                else { ScheduleManager.DeleteAlarmManager(mContext, mCursor.getInt(0)); }
            }
        }
        catch (CursorIndexOutOfBoundsException ex) { ex.printStackTrace(); }
        finally { mCursor.close(); }
    }
}

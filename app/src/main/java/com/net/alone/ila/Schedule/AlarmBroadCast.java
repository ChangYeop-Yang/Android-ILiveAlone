package com.net.alone.ila.Schedule;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import com.net.alone.ila.Basic.Etc;
import com.net.alone.ila.Basic.Weather;
import com.net.alone.ila.Location.LocationManagers;
import com.net.alone.ila.Philips.PhilipsHueColorManager;
import com.net.alone.ila.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mari on 2015-12-09.
 */
public class AlarmBroadCast extends BroadcastReceiver
{
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent)
    {
         /* PowerManager */
        final PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "I Live Alone.");

        /* Notification */
        Notification.Builder mBuilder = new Notification.Builder(context);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setAutoCancel(true);

        /* NotificationManager */
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        /* Intent Value */
        int mColor[] = null;
        boolean mPower = true;

        if (intent.getBooleanExtra("Menu", false))
        {
            /* Notification Builder */
            mBuilder.setSmallIcon(R.drawable.ic_weather);
            mBuilder.setContentTitle("오늘의 날씨"); /* TITLE */

            /* Double */
            final double mDouble[] = new LocationManagers(context).SettingWeatherLocationManager();

            try
            {
                /* ArrayList */
                final ArrayList<String> mArrayList = new Weather(context, mDouble[0], mDouble[1]).execute().get();

                /* Intent */
                mColor = Weather.setIconWeather(context, mArrayList.get(1), null);
                mPower = true;

                /* InboxStyle */
                Notification.InboxStyle mNotification = new Notification.InboxStyle(mBuilder);
                mNotification.setSummaryText("날씨정보를 확인해주세요.");
                mNotification.addLine("온도 : " + mArrayList.get(0) + "℃");
                mNotification.addLine("상태 : " + mArrayList.get(1));
                mNotification.addLine("강수확률 : " + mArrayList.get(2) + "%");
                mNotification.addLine("바람뱡향 : " + mArrayList.get(3));
                mNotificationManager.notify(3, mNotification.build());
            } catch (InterruptedException e) { e.printStackTrace(); } catch (ExecutionException e) { e.printStackTrace(); }
        }
        else
        {
            /* Notification Builder */
            mBuilder.setSmallIcon(R.drawable.ic_calendar_white);
            mBuilder.setContentTitle(String.format("%s 알람 종료", intent.getStringExtra("Name")));

            /* Intent Value */
            mColor = intent.getIntArrayExtra("Color");
            mPower = intent.getBooleanExtra("Power", true);

            if(intent.getIntArrayExtra("Repeat") != null)
            {
                /* Calendar */
                final int mCalendarDate[] = intent.getIntArrayExtra("Repeat");
                final Calendar mCalendar = Calendar.getInstance();
                /* Checking Calendar */
                if(mCalendarDate[mCalendar.get(Calendar.DAY_OF_WEEK)-1] == 0) { return; }
            }

            /* NotificationManager */
            mNotificationManager.notify(2, mBuilder.build());
        }

        /* Philips Connected State */
        if(PhilipsHueColorManager.EnabledHue())
        {
            /* Philips Color Change Method */
            if(mPower) { PhilipsHueColorManager.ChangeALLHueLampColorLamp(mColor); }
            /* Philips Power Change Method */
            else { PhilipsHueColorManager.ChangeALLPowerHueLamp(mPower); }
        }
        /* Philips UnConnected State */
        else { PhilipsHueColorManager.WeakUPHue(context, mColor, mPower); }

        /* Vibrate Method */
        Etc.Vibrate(context, new long[]{200, 200, 500, 300});

        /* PowerManager Release */
        mWakeLock.acquire(); mWakeLock.release();
    }
}

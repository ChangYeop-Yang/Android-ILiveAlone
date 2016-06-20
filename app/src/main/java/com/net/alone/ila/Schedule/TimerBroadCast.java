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
import com.net.alone.ila.Philips.PhilipsHueColorManager;
import com.net.alone.ila.R;

/**
 * Created by Mari on 2015-12-09.
 */
public class TimerBroadCast extends BroadcastReceiver
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

        /* Notification Builder */
        mBuilder.setSmallIcon(R.drawable.ic_timer_white);
        mBuilder.setContentTitle("타이머 종료");

        /* Intent */
        final int mColor[] = intent.getIntArrayExtra("Color");
        final boolean mPower = intent.getBooleanExtra("Power", true);

        /* NotificationManager */
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

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

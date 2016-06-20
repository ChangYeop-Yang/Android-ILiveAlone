package com.net.alone.ila.Basic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Mari on 2015-12-06.
 */
public class BootBroadCast extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            /* Start Service */
            context.startService(new Intent(context, BootService.class));
        }
    }
}

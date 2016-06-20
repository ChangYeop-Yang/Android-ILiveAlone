package com.net.alone.ila.Philips;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHSchedule;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Mari on 2015-12-15.
 */
public class PhilipsHueScheduleManager
{
    /* Create Hue ON Schedule Method */
    public static void CreateHueOnSchedule(int mHour, int mMinute, String mID)
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        if(mPHHueSDK != null)
        {
            /* PHBridge */
            final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();
            if (mPHBridge != null)
            {
                /* Calendar */
                final Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                mCalendar.set(Calendar.MINUTE, mMinute);

                /* PHLight */
                final List<PHLight> mPHLight = mPHBridge.getResourceCache().getAllLights();
                for (PHLight mLight : mPHLight)
                {
                    /* PHLightState */
                    final PHLightState mPHLightState = new PHLightState();
                    mPHLightState.setOn(true);
                    mPHLightState.setBrightness(255);

                    /* Float */
                    final float mFloat[] = PHUtilities.calculateXYFromRGB(255, 255, 255, mLight.getModelNumber());
                    mPHLightState.setX(mFloat[0]);
                    mPHLightState.setY(mFloat[1]);

                    /* PHSchedule */
                    final PHSchedule mPHSchedule = new PHSchedule(mID);
                    mPHSchedule.setRecurringDays(PHSchedule.RecurringDay.RECURRING_ALL_DAY.getValue());
                    mPHSchedule.setLocalTime(true);
                    mPHSchedule.setLightIdentifier(mLight.getIdentifier());
                    mPHSchedule.setLightState(mPHLightState);
                    mPHSchedule.setDate(mCalendar.getTime());

                    /*PHBridge */
                    mPHBridge.createSchedule(mPHSchedule, null);
                }
            }
        }
    }

    /* Create Hue OFF Schedule */
    public static void CreateHueOffSchedule(int mHour, int mMinute, String mID)
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        if(mPHHueSDK != null)
        {
            /* PHBridge */
            final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();
            if (mPHBridge != null)
            {
                /* Calendar */
                final Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                mCalendar.set(Calendar.MINUTE, mMinute);

                /* PHLight */
                final List<PHLight> mPHLight = mPHBridge.getResourceCache().getAllLights();
                for (PHLight mLight : mPHLight)
                {
                    /* PHLightState */
                    final PHLightState mPHLightState = new PHLightState();
                    mPHLightState.setOn(false);

                    /* PHSchedule */
                    final PHSchedule mPHSchedule = new PHSchedule(mID);
                    mPHSchedule.setRecurringDays(PHSchedule.RecurringDay.RECURRING_ALL_DAY.getValue());
                    mPHSchedule.setLocalTime(true);
                    mPHSchedule.setLightIdentifier(mLight.getIdentifier());
                    mPHSchedule.setLightState(mPHLightState);
                    mPHSchedule.setDate(mCalendar.getTime());

                    /*PHBridge */
                    mPHBridge.createSchedule(mPHSchedule, null);
                }
            }
        }
    }

    /* Delete Hue Schedule Method */
    public static void DeleteHueSchedule()
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        if(mPHHueSDK != null)
        {
            /* PHBridge */
            final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();
            if (mPHBridge != null)
            { for(PHSchedule mPHSchedule : mPHBridge.getResourceCache().getAllSchedules(true)) { mPHBridge.removeSchedule(mPHSchedule.getIdentifier(), null); } }
        }
    }
}

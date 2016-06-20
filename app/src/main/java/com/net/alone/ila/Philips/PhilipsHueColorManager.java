package com.net.alone.ila.Philips;

import android.content.Context;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;

/**
 * Created by Mari on 2015-12-02.
 */
public class PhilipsHueColorManager
{
    /* Philips Hue Enabled Method */
    public static boolean EnabledHue()
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        /* PHHBridge */
        if(mPHHueSDK.getSelectedBridge() == null) { return false; } else { return true; }
    }

    /* Philips Hue WeakUP Method */
    public static void WeakUPHue(Context mContext, final int[] mColor, final boolean mBoolean)
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.getInstance();
        mPHHueSDK.getNotificationManager().registerSDKListener(new PHSDKListener()
        {
            @Override public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {}
            @Override public void onBridgeConnected(PHBridge phBridge, String s)
            {
                mPHHueSDK.setSelectedBridge(phBridge);
                mPHHueSDK.enableHeartbeat(phBridge, PHHueSDK.HB_INTERVAL);
                ChangeOffHueColor(mBoolean, mColor, phBridge);
            }
            @Override public void onAuthenticationRequired(PHAccessPoint phAccessPoint) { mPHHueSDK.startPushlinkAuthentication(phAccessPoint); }
            @Override public void onAccessPointsFound(List<PHAccessPoint> list) {}
            @Override public void onError(int i, String s) {}
            @Override public void onConnectionResumed(PHBridge phBridge) {}
            @Override public void onConnectionLost(PHAccessPoint phAccessPoint) {}
            @Override public void onParsingErrors(List<PHHueParsingError> list) {}
        });

        /* HueSharedPreferences */
        final HueSharedPreferences mHueSharedPreferences = HueSharedPreferences.getInstance(mContext);
        final String lastIpAddress = mHueSharedPreferences.getLastConnectedIPAddress();
        final String lastUserName = mHueSharedPreferences.getUsername();

        /* Checking HueSharedPreferences */
        if(lastIpAddress != null && !lastUserName.equals("")) /* Register */
        {
            /* PHAccessPoint */
            PHAccessPoint mPHAccessPoint = new PHAccessPoint();
            mPHAccessPoint.setIpAddress(lastIpAddress); /* IPAddress Setting */
            mPHAccessPoint.setUsername(lastUserName); /* UserName Setting */
            if(!mPHHueSDK.isAccessPointConnected(mPHAccessPoint)) { mPHHueSDK.connect(mPHAccessPoint); }
        }
    }

    /* Philips Hue OffLise Change Method */
    private static void ChangeOffHueColor(boolean mBoolean, int[] mColor, PHBridge mPHBridge)
    {
        /* PHBridge */
        if (mPHBridge != null)
        {
            /* PHLight */
            final List<PHLight> mPHLight = mPHBridge.getResourceCache().getAllLights();
            for (PHLight mLight : mPHLight)
            {
                /* PHLightState */
                final PHLightState mPHLightState = new PHLightState();
                mPHLightState.setOn(mBoolean);
                if(mBoolean)
                {
                    mPHLightState.setBrightness(200);
                    /* Float */
                    final float XY[] = PHUtilities.calculateXYFromRGB(mColor[0], mColor[1], mColor[2], mLight.getModelNumber());
                    mPHLightState.setX(XY[0]);
                    mPHLightState.setY(XY[1]);
                }
                mPHBridge.updateLightState(mLight, mPHLightState);
            }
        }
    }

    /* Philips Hue ALL Color Change Method */
    public static void ChangeALLHueLampColorLamp(int[] mColor)
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        if(mPHHueSDK != null)
        {
            /* PHBridge */
            final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();
            if(mPHBridge != null)
            {
                /* PHLight */
                final List<PHLight> mPHLight = mPHBridge.getResourceCache().getAllLights();
                for(PHLight mLight : mPHLight)
                {
                    /* PHLightState */
                    final PHLightState mPHLightState = new PHLightState();
                    mPHLightState.setOn(true);
                    /* Float */
                    final float XY[] = PHUtilities.calculateXYFromRGB(mColor[0], mColor[1], mColor[2], mLight.getModelNumber());
                    mPHLightState.setX(XY[0]);
                    mPHLightState.setY(XY[1]);
                    mPHBridge.updateLightState(mLight, mPHLightState);
                }
            }
        }
    }

    /* Philips Hue ALL LUX Change Method */
    public static void ChangeALLHueLUXLamp(int mLUX)
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        if (mPHHueSDK != null)
        {
            /* PHBridge */
            final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();
            if (mPHBridge != null)
            {
                /* Light Each */
                final List<PHLight> mPHLight = mPHBridge.getResourceCache().getAllLights();
                for (PHLight mLight : mPHLight)
                {
                    /* PHLightState */
                    final PHLightState mPHLightState = new PHLightState();
                    mPHLightState.setOn(true);
                    mPHLightState.setBrightness(mLUX); /* Hue Lamp 밝기 조절 */
                    mPHBridge.updateLightState(mLight, mPHLightState);
                }
            }
        }
    }

    /* Philips Hue Lamp ALL Power Method */
    public static void ChangeALLPowerHueLamp(boolean mBoolean)
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        if (mPHHueSDK != null)
        {
            /* PHBridge */
            final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();
            if (mPHBridge != null)
            {
                /* Light Each */
                final List<PHLight> mPHLight = mPHBridge.getResourceCache().getAllLights();
                for (PHLight mLight : mPHLight)
                {
                    /* PHLightState */
                    final PHLightState mPHLightState = new PHLightState();
                    mPHLightState.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE.EFFECT_NONE);
                    mPHLightState.setAlertMode(PHLight.PHLightAlertMode.ALERT_NONE);
                    mPHLightState.setOn(mBoolean);
                    mPHBridge.updateLightState(mLight, mPHLightState);
                }
            }
        }
    }

    /* Philips Hue Lamp LUX + Color Method */
    public static void ChangeHueColorLUXLamp(PHLight mLight, int[] mColor, int mLUX)
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        if(mPHHueSDK != null)
        {
            /* PHBridge */
            final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();
            if(mPHBridge != null)
            {
                /* PHLightState */
                final PHLightState mPHLightState = new PHLightState();
                mPHLightState.setBrightness(mLUX); /* Hue Lamp 밝기 조절 */
                mPHLightState.setOn(true);
                /* Float */
                final float XY[] = PHUtilities.calculateXYFromRGB(mColor[0], mColor[1], mColor[2], mLight.getModelNumber());
                mPHLightState.setX(XY[0]);
                mPHLightState.setY(XY[1]);
                mPHBridge.updateLightState(mLight, mPHLightState);
            }
        }
    }

    /* Philips Hue Lamp Power Method */
    public static void ChangePowerHueLamp(PHLight mPHLight, boolean mBoolean)
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        if(mPHHueSDK != null)
        {
            /* PHBridge */
            final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();
            if (mPHBridge != null)
            {

                /* PHLightState */
                final PHLightState mPHLightState = new PHLightState();
                mPHLightState.setOn(mBoolean);
                mPHLightState.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE);
                mPHLightState.setAlertMode(PHLight.PHLightAlertMode.ALERT_NONE);
                /* PHBridge */
                mPHBridge.updateLightState(mPHLight, mPHLightState);
            }
        }
    }

    /* Philips Hue Lamp Color Method */
    public static void ChangeColorHueLamp(PHLight mPHLight, int[] mColor)
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        if(mPHHueSDK != null)
        {
            /* PHBridge */
            final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();
            if(mPHBridge != null)
            {
                /* PHLightState */
                final PHLightState mPHLightState = new PHLightState();
                mPHLightState.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE.EFFECT_NONE);
                mPHLightState.setAlertMode(PHLight.PHLightAlertMode.ALERT_NONE);
                mPHLightState.setOn(true);

                /* Float */
                final float mFloat[] = PHUtilities.calculateXYFromRGB(mColor[0], mColor[1], mColor[2], mPHLight.getModelNumber());
                mPHLightState.setX(mFloat[0]);
                mPHLightState.setY(mFloat[1]);

                /* PHBridge*/
                mPHBridge.updateLightState(mPHLight, mPHLightState);
            }
        }
    }

    /* Philips Hue Lamp LUX Method */
    public static void ChangeLUXHueLamp(PHLight mPHLight, int mLUX)
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        if(mPHHueSDK != null)
        {
            /* PHBridge */
            final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();
            if (mPHBridge != null)
            {
                /* PHLightState */
                final PHLightState mPHLightState = new PHLightState();
                mPHLightState.setOn(true);
                mPHLightState.setBrightness(mLUX);
                mPHLightState.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE.EFFECT_NONE);
                mPHLightState.setAlertMode(PHLight.PHLightAlertMode.ALERT_NONE);
                /* PHLight Bridge */
                mPHBridge.updateLightState(mPHLight, mPHLightState);
            }
        }
    }
}

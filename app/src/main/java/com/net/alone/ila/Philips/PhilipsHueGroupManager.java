package com.net.alone.ila.Philips;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mari on 2015-12-15.
 */
public class PhilipsHueGroupManager
{
    /* Check Philips Hue Group Manager Method */
    public static PHGroup CheckGroupHue(String mGroupName)
    {
        /* PHGroup */
        PHGroup mPHGroup = null;

        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        if (mPHHueSDK != null)
        {
            /* PHBridge */
            final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();
            if (mPHBridge != null)
            {
                for(PHGroup mGroup : mPHBridge.getResourceCache().getAllGroups())
                { if(mGroup.getName().equals(mGroupName)) { mPHGroup = mGroup; break; } }
            }
        }

        return mPHGroup;
    }

    /* Create Philips Hue Group Manager Method */
    public static void CreateGroupHue(String mGroup)
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        if (mPHHueSDK != null)
        {
            /* PHBridge */
            final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();
            if (mPHBridge != null)
            {
                /* PHGroup */
                final PHGroup mPHGroup = new PHGroup(mGroup, mGroup);

                /* ArrayList */
                final List<String> mIdentifiers = new ArrayList<String>(10);

                /* PHLight */
                final List<PHLight> mPHLight = mPHBridge.getResourceCache().getAllLights();
                for (PHLight mLight : mPHLight) { mIdentifiers.add(mLight.getIdentifier()); }

                /* PHGroup */
                mPHGroup.setLightIdentifiers(mIdentifiers);
                mPHBridge.createGroup(mPHGroup, null);
            }
        }
    }

    /* Delete Philips Hue Group Manager Method */
    public static void DeleteGroupHue(String mGroup)
    {
        /* PHHueSDK */
        final PHHueSDK mPHHueSDK = PHHueSDK.create();
        if (mPHHueSDK != null)
        {
            /* PHBridge */
            final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();
            if (mPHBridge != null) { mPHBridge.deleteGroup(mGroup, null); }
        }
    }
}

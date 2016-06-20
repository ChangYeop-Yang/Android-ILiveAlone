package com.net.alone.ila;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.net.alone.ila.Basic.Adapter.ConnectionAdapter;
import com.net.alone.ila.Philips.HueSharedPreferences;
import com.net.alone.ila.Philips.PHWizardAlertDialog;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

import java.util.List;

/**
 * Created by Mari on 2015-11-29.
 */
public class ConnectionActivity extends BaseActivity implements PHSDKListener
{
    /* ConnectionAdapter */
    private ConnectionAdapter mConnectionAdapter = null;

    /* HueSharedPreferences */
    private HueSharedPreferences mHueSharedPreferences = null;

    /* Context */
    private Context mContext = null;

    /* PHHueSDK */
    private PHHueSDK mPHHueSDK = null;

    /* Boolean */
    private boolean lastSearchWasIPScan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        /* setToolBar Method */
        setToolbar("연결 화면", "Philips Hue Day!");

        /* Context */
        mContext = getApplicationContext();

        /* Philips Hue Connecting */
        mPHHueSDK = PHHueSDK.create();
        /* Philips Hue Name Setting */
        mPHHueSDK.setAppName(getString(R.string.app_name)); /* Philips Hue APP Name */
        mPHHueSDK.setDeviceName(android.os.Build.MODEL); /* Philips Hue Device Name */
        /* Philips Register PHSDKListener */
        mPHHueSDK.getNotificationManager().registerSDKListener(this);

        if(mPHHueSDK.getSelectedBridge() != null) { startActivity(new Intent(mContext, MainActivity.class)); }
        else
        {
            /* ConnectionAdapter */
            mConnectionAdapter = new ConnectionAdapter(ConnectionActivity.this, mPHHueSDK.getAccessPointsFound());

            /* ListView */
            final ListView mListView = (ListView)findViewById(R.id.ConnectionListView);
            settingListView(mListView);
            mListView.setAdapter(mConnectionAdapter);

            /* Quick Preference Connection Method */
            quickPreferenceConnection();
        }
    }

    /* Setting ListView Method */
    private void settingListView(ListView mListView)
    {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                 /* PHAccessPoint */
                final PHAccessPoint mPHAccessPoint = (PHAccessPoint) mConnectionAdapter.getItem(position);
                /* PHBridge */
                final PHBridge mPHBridge = mPHHueSDK.getSelectedBridge();

                if (mPHBridge != null)
                {
                    /* String */
                    final String mConnectedIP = mPHBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
                    if (mConnectedIP != null)
                    {
                        mPHHueSDK.disableHeartbeat(mPHBridge);
                        mPHHueSDK.disconnect(mPHBridge);
                    }
                }
                /* PHWizardAlertDialog */
                PHWizardAlertDialog.getInstance().showProgressDialog(R.string.Connection, ConnectionActivity.this);
                mPHHueSDK.connect(mPHAccessPoint);
            }
        });
    }

    /* Quick Preference Connection Method */
    private void quickPreferenceConnection()
    {
        /* HueSharedPreferences */
        mHueSharedPreferences = HueSharedPreferences.getInstance(ConnectionActivity.this);
        final String lastIpAddress = mHueSharedPreferences.getLastConnectedIPAddress();
        final String lastUserName = mHueSharedPreferences.getUsername();
            /* Checking HueSharedPreferences */
        if(lastIpAddress != null && !lastUserName.equals("")) /* Register */
        {
            /* PHAccessPoint */
            PHAccessPoint mPHAccessPoint = new PHAccessPoint();
            mPHAccessPoint.setIpAddress(lastIpAddress); /* IPAddress Setting */
            mPHAccessPoint.setUsername(lastUserName); /* UserName Setting */
            if(!mPHHueSDK.isAccessPointConnected(mPHAccessPoint))
            {
                /* PHWizardAlertDialog */
                PHWizardAlertDialog.getInstance().showProgressDialog(R.string.Connection, ConnectionActivity.this);
                /* Philips Bridge Connect */
                mPHHueSDK.connect(mPHAccessPoint);
            }
        }
        else /* UnRegister */
        {
            /* Philips Hue Bridge */
            PHWizardAlertDialog.getInstance().showProgressDialog(R.string.Connection, ConnectionActivity.this);
            final PHBridgeSearchManager mPHBridgeSearchManager = (PHBridgeSearchManager) mPHHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
            mPHBridgeSearchManager.search(true, true);
        }
    }

    @Override public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {}

    @Override /* Philips Hue Bridge Connected */
    public void onBridgeConnected(PHBridge phBridge, String s)
    {
        /* Philips Hue Bridge Select */
        mPHHueSDK.setSelectedBridge(phBridge);
        mPHHueSDK.enableHeartbeat(phBridge, PHHueSDK.HB_INTERVAL);
        mPHHueSDK.getLastHeartbeat().put(phBridge.getResourceCache().getBridgeConfiguration().getIpAddress(), System.currentTimeMillis());
        mPHBridge = phBridge;

        /* HueSharedPreferences */
        mHueSharedPreferences.setLastConnectedIPAddress(phBridge.getResourceCache().getBridgeConfiguration().getIpAddress());
        mHueSharedPreferences.setUsername(s);

        /* PHWizardAlertDialog Dismiss */
        PHWizardAlertDialog.getInstance().closeProgressDialog();

        /* startActivity */
        startActivity(new Intent(mContext, MainActivity.class)); finish();
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint phAccessPoint)
    {
        mPHHueSDK.startPushlinkAuthentication(phAccessPoint);
    }

    @Override /* Philips Hue Bridge Find List Method */
    public void onAccessPointsFound(List<PHAccessPoint> list)
    {
        /* PHWizardAlertDialog */
        PHWizardAlertDialog.getInstance().closeProgressDialog();
        if(list != null && list.size() > 0)
        {
            /* AccessPoint Clear */
            mPHHueSDK.getAccessPointsFound().clear();
            /* AccessPoint Save */
            mPHHueSDK.getAccessPointsFound().addAll(list);

            /* UI Thread */
            runOnUiThread(new Runnable()
            {
                /* Hue Bridge 검색 된 정보를 Adapter 에 저장 */
                @Override public void run()
                {
                    Toast.makeText(mContext, "Bridge 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
                    mConnectionAdapter.updateData(mPHHueSDK.getAccessPointsFound());
                }
            });
        }
    }

    @Override
    public void onError(int i, final String s)
    {
        Log.e("on Error Called : ", s);
        if(i == PHHueParsingError.NO_CONNECTION) { Log.e("Philips", "On No Connection."); }
        else if(i == PHHueParsingError.AUTHENTICATION_FAILED || i == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED)
        { PHWizardAlertDialog.getInstance().closeProgressDialog(); }
        else if(i == PHHueParsingError.BRIDGE_NOT_RESPONDING)
        {
            Log.e("Philips", "Bridge Not Responding.");
            PHWizardAlertDialog.getInstance().closeProgressDialog();
            ConnectionActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run() { PHWizardAlertDialog.showErrorDialog(ConnectionActivity.this, s, R.string.Connection); }
            });
        }
        else if(i == PHMessageType.BRIDGE_NOT_FOUND)
        {
            if(!lastSearchWasIPScan)
            {
                mPHHueSDK = PHHueSDK.getInstance();
                final PHBridgeSearchManager mPHBridgeSearchManager = (PHBridgeSearchManager) mPHHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                mPHBridgeSearchManager.search(false, false, true);
                lastSearchWasIPScan = true;
            }
            else
            {
                PHWizardAlertDialog.getInstance().closeProgressDialog();
                ConnectionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() { PHWizardAlertDialog.showErrorDialog(ConnectionActivity.this, s, R.string.Connection); }
                });
            }
        }
    }

    @Override
    public void onConnectionResumed(PHBridge phBridge)
    {
        if(ConnectionActivity.this.isFinishing()) { return; }
        else
        {
            Log.e("onConnectionResumed", phBridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            mPHHueSDK.getLastHeartbeat().put(phBridge.getResourceCache().getBridgeConfiguration().getIpAddress(), System.currentTimeMillis());
            for(int count = 0; count < mPHHueSDK.getDisconnectedAccessPoint().size(); count++)
            {
                if(mPHHueSDK.getDisconnectedAccessPoint().get(count).getIpAddress().equals(phBridge.getResourceCache().getBridgeConfiguration().getIpAddress()))
                { mPHHueSDK.getDisconnectedAccessPoint().remove(count); }
            }
        }
    }

    @Override
    public void onConnectionLost(PHAccessPoint phAccessPoint)
    {
        Log.e("onConnectionLost : ", phAccessPoint.getIpAddress());
        if(!mPHHueSDK.getDisconnectedAccessPoint().contains(phAccessPoint))
        { mPHHueSDK.getDisconnectedAccessPoint().add(phAccessPoint); }
    }

    @Override public void onParsingErrors(List<PHHueParsingError> list) {}
}

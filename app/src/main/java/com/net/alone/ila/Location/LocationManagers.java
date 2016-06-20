package com.net.alone.ila.Location;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Mari on 2015-12-02.
 */
public class LocationManagers
{
    /* Context */
    private Context mContext = null;

    /* LocationManager */
    private LocationManager mLocationManager = null;

    public LocationManagers(Context mContext) { this.mContext = mContext; }

    /* Setting Location Manager Method */
    public double[] SettingWeatherLocationManager()
    {
        /* LocationManager */
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        /* String */
        String mProvider = null;

        /*
        * mLocationManager.NETWORK_PROVIDER : 기지국 기반 위치조회
        * LocationManager.GPS_PROVIDER : GPS 기반 위치조회
        */
        if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true) { mProvider = mLocationManager.NETWORK_PROVIDER; }
        else { mProvider = LocationManager.GPS_PROVIDER; }

        /* Location */
        final Location mLocation = mLocationManager.getLastKnownLocation(mProvider);

        if(mLocation != null)
        {
            /* Write SharedPreference Method */
            WritePreferencesPoint(mLocation.getLatitude(), mLocation.getLongitude());
            return new double[]{mLocation.getLatitude(), mLocation.getLongitude()};
        }
        else
        {
            /* Toast */ Toast.makeText(mContext, "GPS 비활성 상태이거나 장시간 미사용으로 인하여 위치가 다를 수가 있습니다.", Toast.LENGTH_SHORT).show();
            /* LocationManager */ mLocationManager.requestLocationUpdates(mProvider, 0, 0, mLocationListener);
            /* Read SharedPreference Method */ return ReadSharedPreferencesPoint();
        }
    }

    /* Write SharedPreferences Method */
    private void WritePreferencesPoint(double mMapX, double mMapY)
    {
        /* SharedPreferences Write */
        final SharedPreferences mSharedPreferences = mContext.getSharedPreferences("Gps", 0);

        /* SharedPreferences.Editor */
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("MapX", Double.toString(mMapX));
        mEditor.putString("MapY", Double.toString(mMapY));
        mEditor.commit();
    }

    /* Read SharedPreferences Method */
    private double[] ReadSharedPreferencesPoint()
    {
        /* SharedPreferences Write */
        final SharedPreferences mSharedPreferences = mContext.getSharedPreferences("Gps", 0);
        return new double[]{ Double.valueOf(mSharedPreferences.getString("MapX", "0.0")).doubleValue(), Double.valueOf(mSharedPreferences.getString("MapY", "0.0")).doubleValue() };
    }

    /* LocationListener */
    private LocationListener mLocationListener = new LocationListener()
    {
        @Override public void onLocationChanged(Location location) { WritePreferencesPoint(location.getLatitude(), location.getLongitude()); mLocationManager.removeUpdates(mLocationListener); }
        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override public void onProviderEnabled(String provider) {}
        @Override public void onProviderDisabled(String provider) {}
    };
}

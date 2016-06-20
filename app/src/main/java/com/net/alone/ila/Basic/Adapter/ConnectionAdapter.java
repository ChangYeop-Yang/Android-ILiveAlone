package com.net.alone.ila.Basic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.net.alone.ila.R;
import com.philips.lighting.hue.sdk.PHAccessPoint;

import java.util.List;

/**
 * Created by Mari on 2015-11-30.
 */
public class ConnectionAdapter extends BaseAdapter
{
    /* LayoutInflater */
    private LayoutInflater mLayoutInflater = null;
    /* List */
    private List<PHAccessPoint> mList = null;

    public ConnectionAdapter(Context mConext, List<PHAccessPoint> mList)
    {
        mLayoutInflater = LayoutInflater.from(mConext);
        this.mList = mList;
    }

    /* Inner Class */
    private class BridgeListItem
    {
        /* TextView */
        private TextView mIPAddress = null;
        private TextView mMACAddress = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        /* BridgeListItem */
        BridgeListItem mBridgeListItem = null;

        if(convertView == null)
        {
            convertView = mLayoutInflater.inflate(R.layout.custom_connection_adapter, null);

            /* BridgeListItem */
            mBridgeListItem = new BridgeListItem();
            mBridgeListItem.mIPAddress = (TextView) convertView.findViewById(R.id.CustomConnectionIP);
            mBridgeListItem.mMACAddress = (TextView) convertView.findViewById(R.id.CustomConnectionMAC);
            convertView.setTag(mBridgeListItem);
        } else { mBridgeListItem = (BridgeListItem) convertView.getTag(); }

        /* PHAccessPoint */
        final PHAccessPoint mPHAccessPoint = mList.get(position);
        mBridgeListItem.mIPAddress.setText(mPHAccessPoint.getIpAddress());
        mBridgeListItem.mMACAddress.setText(mPHAccessPoint.getMacAddress());

        return convertView;
    }

    public void updateData(List<PHAccessPoint> mList) { this.mList = mList; notifyDataSetChanged(); }
    @Override public int getCount() { return mList.size(); }
    @Override public Object getItem(int position) { return mList.get(position); }
    @Override public long getItemId(int position) { return position; }
}

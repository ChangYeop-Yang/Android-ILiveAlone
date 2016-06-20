package com.net.alone.ila.Basic.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.net.alone.ila.Basic.Etc;
import com.net.alone.ila.Basic.SQLite;
import com.net.alone.ila.R;
import com.net.alone.ila.Schedule.ScheduleManager;

import java.util.ArrayList;

/**
 * Created by Mari on 2015-12-04.
 */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder>
{
    /* Context */
    private Context mContext = null;

    /* ArrayList */
    private ArrayList<Alarm> mArrayList = null;

    /* AlarmAdapter */
    private AlarmAdapter mAlarmAdapter = this;

    public AlarmAdapter(Context mContext, ArrayList<Alarm> mArrayList) { this.mContext = mContext; this.mArrayList = mArrayList; }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        /* TextView */
        holder.mNameTextView.setText(mArrayList.get(position).mName);
        holder.mTimeTextView.setText(mArrayList.get(position).mHour + "시 " + mArrayList.get(position).mMinute + "분");
        for(int count = 0, mLength = holder.mTextView.length; count < mLength; count++) { if(mArrayList.get(position).mSchedule[count] == 1) { holder.mTextView[count].setTextColor(Color.rgb(139,195,74)); } }

        /* Linear Layout */
        holder.mLinearLayout.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                /* Vibrate */
                Etc.Vibrate(mContext, new long[]{100, 200, 300});

                /* SnackBar */
                Snackbar.make(v, mArrayList.get(position).mName, Snackbar.LENGTH_SHORT).setAction("삭제", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* ScheduleManager DeleteAlarm Method */
                        ScheduleManager.DeleteAlarmManager(mContext, mArrayList.get(position).mID);

                        /* SQLite Delete Method */
                        SQLite.DeleteValue(mContext, "alarm_db", "_id=?", new String[]{String.format("%d", mArrayList.get(position).mID)});

                        /* Adapter */
                        mArrayList.remove(position);
                        mAlarmAdapter.notifyItemRemoved(position);
                        mAlarmAdapter.notifyDataSetChanged();
                    }
                }).show();
                return false;
            }
        });

        /* ImageView */
        if(mArrayList.get(position).mActivity == 1) { holder.mImageButtonView.setImageResource(R.drawable.ic_alarm_green); } else { holder.mImageButtonView.setImageResource(R.drawable.ic_alarm_64); }
        holder.mImageButtonView.setOnClickListener(new View.OnClickListener()
        {
            /* ContentValue */
            final ContentValues mContentValues = new ContentValues();

            @Override
            public void onClick(View v)
            {
                /* Vibrate */
                Etc.Vibrate(mContext, new long[]{100, 200, 300});

                if(mArrayList.get(position).mActivity == 1)
                {
                    /* ImageButton Set Image */
                    holder.mImageButtonView.setImageResource(R.drawable.ic_alarm_64);
                    mArrayList.get(position).mActivity = 0;

                    /* AlarmManager Cancel */
                    ScheduleManager.DeleteAlarmManager(mContext, mArrayList.get(position).mID);

                    /* ContentValue */
                    mContentValues.put("activity", mArrayList.get(position).mActivity);

                    /* Toast */
                    Toast.makeText(mContext, "알람 해제", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    /* ImageButton Set Image */
                    holder.mImageButtonView.setImageResource(R.drawable.ic_alarm_green);
                    mArrayList.get(position).mActivity = 1;

                    /* ContentValue */
                    mContentValues.put("activity", mArrayList.get(position).mActivity);

                    if(mArrayList.get(position).mRepeat == 0) { mArrayList.get(position).mSchedule = null; }

                    /* AlarmManager Create Method */
                    ScheduleManager.CreateAlarm(mContext, mArrayList.get(position).mName, mArrayList.get(position).mID, mArrayList.get(position).mHour, mArrayList.get(position).mMinute,
                            mArrayList.get(position).mSchedule, mArrayList.get(position).mColor, mArrayList.get(position).mPower);
                }

                /* SQLite Alarm Delete Method */
                SQLite.UpdateValue(mContext, "alarm_db", "_id = " + mArrayList.get(position).mID, mContentValues);
            }
        });
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_alarm_adapter, parent, false); return new ViewHolder(mView); }
    @Override public int getItemCount() { return mArrayList.size(); }

    /* ViewHolder */
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        /* TextView */
        private TextView mNameTextView = null;
        private TextView mTimeTextView = null;
        private TextView mTextView[] = null;

        /* ImageView */
        private ImageButton mImageButtonView = null;

        /* LinearLayout */
        private LinearLayout mLinearLayout = null;

        private ViewHolder(View itemView)
        {
            super(itemView);
            /* TextView */
            mNameTextView = (TextView)itemView.findViewById(R.id.AlarmItemTitle);
            mTimeTextView = (TextView)itemView.findViewById(R.id.AlarmTime);
            mTextView = new TextView[]{(TextView)itemView.findViewById(R.id.AlarmItemSunday), (TextView)itemView.findViewById(R.id.AlarmItemMonth), (TextView)itemView.findViewById(R.id.AlarmItemTuesday), (TextView)itemView.findViewById(R.id.AlarmItemWednesday), (TextView)itemView.findViewById(R.id.AlarmItemThursday)
            , (TextView)itemView.findViewById(R.id.AlarmItemFriday), (TextView)itemView.findViewById(R.id.AlarmItemSaturday)};
            /* LinearLayout */
            mLinearLayout = (LinearLayout)itemView.findViewById(R.id.AlarmItemLayout);
            /* ImageView */
            mImageButtonView = (ImageButton)itemView.findViewById(R.id.AlarmItemImageView);
        }
    }

    public static class Alarm
    {
        /* String */
        private String mName = null;
        /* Integer */
        private int mID = 0;
        private int mHour = 0, mMinute = 0;
        private int mRepeat = 0, mActivity = 0;
        private int mSchedule[] = null;
        private int mColor[] = null;
        /* Boolean */
        private boolean mPower = false;

        public Alarm(String mName, int mID, int mHour, int mMinute, int mRepeat, int mActivity, int[] mSchedule, int[] mColor, int mPower)
        {
            /* String */
            this.mName = mName;
            /* Integer */
            this.mID = mID;
            this.mHour = mHour;
            this.mMinute = mMinute;
            this.mRepeat = mRepeat;
            this.mActivity = mActivity;
            this.mSchedule = mSchedule;
            this.mColor = mColor;
            this.mPower = mPower == 0 ? false : true;
        }
    }
}

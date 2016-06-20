package com.net.alone.ila.Basic.Adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.net.alone.ila.Basic.Etc;
import com.net.alone.ila.Basic.SQLite;
import com.net.alone.ila.R;
import com.net.alone.ila.Schedule.ScheduleManager;

import java.util.ArrayList;

/**
 * Created by Mari on 2015-12-09.
 */
public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.ViewHolder>
{
    /* Context */
    private Context mContext = null;

    /* ArrayList */
    private ArrayList<Timer> mArrayList = null;

    /* TimerAdapter */
    private TimerAdapter mTimerAdapter = this;

    public TimerAdapter(Context mContext, ArrayList<Timer> mArrayList)
    {
        /* Context */
        this.mContext = mContext;
        /* ArrayList */
        this.mArrayList = mArrayList;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        /* TextView */
        holder.mTimerName.setText(mArrayList.get(position).mName);
        holder.mTimerTime.setText(mArrayList.get(position).mTime[0] + "시 " + mArrayList.get(position).mTime[1] + "분 " + mArrayList.get(position).mTime[2] + "초");

        /* Button */
        setButton(holder.mApply, position);
        setButton(holder.mDelete, position);

        /* ImageView */
        Glide.with(mContext).load(mArrayList.get(position).mImg).error(R.drawable.ic_alarm_64).into(holder.mImageView);
    }

    /* Setting Button Method */
    private void setButton(Button mButton, final int mPosition)
    {
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /* Vibrate */
                Etc.Vibrate(mContext, new long[]{100, 200, 300});

                switch (v.getId())
                {
                    /* Timer Apply Button */
                    case (R.id.TimerApply) :
                    {
                        /* Schedule Method */
                        ScheduleManager.CreateSmartTimer(mContext, mArrayList.get(mPosition).mName, mArrayList.get(mPosition).mTime[0], mArrayList.get(mPosition).mTime[1], mArrayList.get(mPosition).mTime[2], mArrayList.get(mPosition).mID, mArrayList.get(mPosition).mBoolean);
                        break;
                    }
                    /* Timer Delete Button */
                    case (R.id.TimerDelete) :
                    {
                        /* SnackBar */
                        Snackbar.make(v, mArrayList.get(mPosition).mName, Snackbar.LENGTH_SHORT).setAction("삭제", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if(mArrayList.get(mPosition).mID < 6) { Toast.makeText(v.getContext(), "기본아이템은 삭제가 불가능합니다.", Toast.LENGTH_SHORT).show(); }
                                else
                                {
                                    /* SQLite Delete Method */
                                    SQLite.DeleteValue(mContext, "timer_db", "_Id=?", new String[]{String.format("%d", mArrayList.get(mPosition).mID)});

                                    /* TimerAdapter */
                                    mArrayList.remove(mPosition);
                                    mTimerAdapter.notifyItemRemoved(mPosition);
                                    mTimerAdapter.notifyDataSetChanged();
                                }
                            }
                        }).show();
                        break;
                    }
                }
            }
        });
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_timer_adapter, parent, false); return new ViewHolder(mView); }
    @Override public int getItemCount() { return mArrayList.size(); }

    /* ViewHolder */
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        /* TextView */
        private TextView mTimerName = null;
        private TextView mTimerTime = null;

        /* ImageView */
        private ImageView mImageView = null;

        /* Button */
        private Button mApply = null;
        private Button mDelete = null;

        private ViewHolder(View itemView)
        {
            super(itemView);

            /* TextView */
            mTimerName = (TextView)itemView.findViewById(R.id.CustomTimerName);
            mTimerTime = (TextView)itemView.findViewById(R.id.CustomTimerTime);

            /* Button */
            mApply = (Button)itemView.findViewById(R.id.TimerApply);
            mDelete = (Button)itemView.findViewById(R.id.TimerDelete);

            /* ImageView */
            mImageView = (ImageView)itemView.findViewById(R.id.CustomTimerImage);
        }
    }

    /* Timer Inner Class */
    public static class Timer
    {
        /* String */
        private String mName = null;

        /* Integer */
        private int mTime[] = null;
        private int mImg = 0;
        private int mID = 0;

        /* Boolean */
        private boolean mBoolean = true;

        public Timer(String mName, int[] mTime, int mImg, int mID, boolean mBoolean)
        {
            /* String */
            this.mName = mName;

            /* Integer */
            this.mTime = mTime;
            this.mImg = mImg;
            this.mID = mID;

            /* Boolean */
            this.mBoolean = mBoolean;
        }
    }
}

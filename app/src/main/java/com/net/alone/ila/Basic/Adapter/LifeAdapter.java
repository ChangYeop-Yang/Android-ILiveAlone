package com.net.alone.ila.Basic.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.net.alone.ila.Basic.Etc;
import com.net.alone.ila.Basic.SQLite;
import com.net.alone.ila.Philips.PhilipsHueColorManager;
import com.net.alone.ila.R;

import java.util.ArrayList;

/**
 * Created by Mari on 2015-12-08.
 */
public class LifeAdapter extends RecyclerView.Adapter<LifeAdapter.ViewHolder>
{
    /* Context */
    private Context mContext = null;

    /* ArrayList */
    private ArrayList<Life> mArrayList = null;

    /* LifeAdapter */
    private LifeAdapter mLifeAdapter = this;

    public LifeAdapter(Context mContext, ArrayList<Life> mArrayList)
    { this.mContext = mContext; this.mArrayList = mArrayList; }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        /* TextView */
        holder.mBookMarkTextView.setText("# " + mArrayList.get(position).mName);
        holder.mCodeTextView.setText(String.format("#%02X%02X%02X", mArrayList.get(position).mColor[0], mArrayList.get(position).mColor[1], mArrayList.get(position).mColor[2]));

        /* ImageView */
        holder.mColorImageView.setBackgroundColor(Color.rgb(mArrayList.get(position).mColor[0], mArrayList.get(position).mColor[1], mArrayList.get(position).mColor[2]));

        /* CardView */
        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                /* Vibrate */
                Etc.Vibrate(mContext, new long[]{100, 200, 300});
                /* SnackBar */
                Snackbar.make(v, mArrayList.get(position).mName, Snackbar.LENGTH_SHORT).setAction("삭제", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(mArrayList.get(position).mID < 3) { Toast.makeText(v.getContext(), "기본아이템은 삭제가 불가능합니다.", Toast.LENGTH_SHORT).show(); }
                        else
                        {
                            /* SQLite Delete Method */
                            SQLite.DeleteValue(mContext, "life_db", "_id=?", new String[]{String.format("%d", mArrayList.get(position).mID)});
                            mArrayList.remove(position);
                            mLifeAdapter.notifyItemRemoved(position);
                            mLifeAdapter.notifyDataSetChanged();
                        }
                    }
                }).show();
                return false;
            }
        });

        /* Button */
        holder.mApply.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { PhilipsHueColorManager.ChangeALLHueLampColorLamp(mArrayList.get(position).mColor); }
        });
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_bookmark_adapter, parent, false); return new ViewHolder(mView); }
    @Override public int getItemCount() { return mArrayList.size(); }

    /* ViewHolder */
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        /* TextView */
        private TextView mCodeTextView = null;
        private TextView mBookMarkTextView = null;

        /* Button */
        private Button mApply = null;

        /* ImageView */
        private ImageView mColorImageView = null;

        /* CardView */
        private CardView mCardView = null;

        private ViewHolder(View itemView)
        {
            super(itemView);

            /* TextView */
            mCodeTextView = (TextView)itemView.findViewById(R.id.CustomBookMarkText);
            mBookMarkTextView = (TextView)itemView.findViewById(R.id.CustomBookMarkColorText);

            /* ImageView */
            mColorImageView = (ImageView)itemView.findViewById(R.id.CustomBookMarkImage);

            /* Button */
            mApply = (Button)itemView.findViewById(R.id.CustomBookMarkApply);

            /* CardView */
            mCardView = (CardView)itemView.findViewById(R.id.CustomBookMarkCardView);
        }
    }

    /* Life Inner Class */
    public static class Life
    {
        /* Integer */
        private int mColor[] = null;
        private int mLUX = 0;
        private int mID = 0;
        /* String */
        private String mName = null;

        public Life(String mName, int[] mColor, int mLUX, int mID)
        {
            /* String */
            this.mName = mName;
            /* Integer */
            this.mColor = mColor;
            this.mLUX = mLUX;
            this.mID = mID;
        }
    }
}

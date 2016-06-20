package com.net.alone.ila.Basic.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.net.alone.ila.Basic.Etc;
import com.net.alone.ila.KidsSaveActivity;
import com.net.alone.ila.R;

import java.util.ArrayList;

/**
 * Created by Mari on 2015-12-13.
 */
public class KidsSaveAdapter extends RecyclerView.Adapter<KidsSaveAdapter.ViewHolder>
{
    /* Context */
    private Context mContext = null;

    /* ArrayList */
    private ArrayList<KidsColor> mColorList = null;

    /* Integer */
    private final static int LIMIT_ITEM = 3;

    public KidsSaveAdapter(Context mContext, ArrayList<KidsColor> mColorList)
    {
        /* Context */
        this.mContext = mContext;
        /* ArrayList */
        this.mColorList = mColorList;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        /* Color */
        final int mColor = mColorList.get(position).mSwatch.getRgb();
        final int mRED = Color.red(mColor);
        final int mGREEN = Color.green(mColor);
        final int mBLUE = Color.blue(mColor);

        /* TextView */
        holder.mCodeTextView.setText(String.format("#%02X%02X%02X", mRED, mGREEN, mBLUE));
        holder.mCodeTextView.setTextColor(mColorList.get(position).mSwatch.getBodyTextColor());
        holder.mBookMarkTextView.setText(String.format("R %d G %d B %d", mRED, mGREEN, mBLUE));

        /* ImageView */
        holder.mColorImageView.setBackgroundColor(Color.rgb(mRED, mGREEN, mBLUE));

        /* Button */
        holder.mApply.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /* Vibreate */
                Etc.Vibrate(mContext, new long[]{100});
                if(LIMIT_ITEM > KidsSaveActivity.mColorList.size())
                {
                    KidsSaveActivity.mColorList.add(String.format("%d %d %d", mRED, mGREEN, mBLUE));
                    /* Toast */ Toast.makeText(mContext, "선택완료", Toast.LENGTH_SHORT).show();
                }
                else
                { Snackbar.make(((Activity)mContext).getCurrentFocus(), "3개의 색상만 선택이 가능합니다.", Snackbar.LENGTH_SHORT).setAction("초기화", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v) { KidsSaveActivity.mColorList.clear(); }
                    }).show(); }
            }
        });
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_bookmark_adapter, parent, false); return new ViewHolder(mView); }
    @Override public int getItemCount() { return mColorList.size(); }

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
        }
    }

    /* LifeColor Inner Class */
    public static class KidsColor
    {
        /* Swatch */
        private Palette.Swatch mSwatch = null;

        public KidsColor(Palette.Swatch mSwatch)
        { this.mSwatch = mSwatch; }
    }
}

package com.net.alone.ila.Basic.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.net.alone.ila.R;

import java.util.ArrayList;

/**
 * Created by Mari on 2015-12-12.
 */
public class LifeSaveAdapter extends RecyclerView.Adapter<LifeSaveAdapter.ViewHolder>
{
    /* Context */
    private Context mContext = null;

    /* ArrayList */
    private ArrayList<LifeColor> mColorList = null;

    /* SeekBar */
    private SeekBar mSeekBar[] = null;

    /* TextView */
    private TextView mTextView = null;

    public LifeSaveAdapter(Context mContext, ArrayList<LifeColor> mColorList, SeekBar[] mSeekBar, TextView mTextView)
    {
        /* Context */
        this.mContext = mContext;
        /* ArrayList */
        this.mColorList = mColorList;
        /* SeekBar */
        this.mSeekBar = mSeekBar;
        /* TextView */
        this.mTextView = mTextView;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        /* Color Integer */
        final int mColor = mColorList.get(position).mSwatch.getRgb();
        final int mRED = Color.red(mColor);
        final int mGREEN = Color.green(mColor);
        final int mBLUE = Color.blue(mColor);

        /* TextView */
        holder.mBookMarkTextView.setText(String.format("R %d G %d B %d", mRED, mGREEN, mBLUE));
        holder.mCodeTextView.setText(String.format("#%02X%02X%02X", mRED, mGREEN, mBLUE));
        holder.mCodeTextView.setTextColor(mColorList.get(position).mSwatch.getBodyTextColor());

        /* ImageView */
        holder.mColorImageView.setBackgroundColor(mColor);

        /* Button */
        holder.mApply.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /* TextView */
                mTextView.setText(String.format("%02X%02X%02X", mRED, mGREEN, mBLUE));
                mTextView.setTextColor(mColor);
                /* SeekBar */
                mSeekBar[0].setProgress(mRED);
                mSeekBar[1].setProgress(mGREEN);
                mSeekBar[2].setProgress(mBLUE);
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
    public static class LifeColor
    {
        /* Swatch */
        private Palette.Swatch mSwatch = null;

        public LifeColor(Palette.Swatch Swatch)
        { this.mSwatch = Swatch; }
    }
}

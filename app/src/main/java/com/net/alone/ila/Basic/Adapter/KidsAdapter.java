package com.net.alone.ila.Basic.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.net.alone.ila.Basic.Etc;
import com.net.alone.ila.Basic.SQLite;
import com.net.alone.ila.Philips.PhilipsHueColorManager;
import com.net.alone.ila.R;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;

import net.daum.mf.speech.api.TextToSpeechClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mari on 2015-12-13.
 */
public class KidsAdapter extends RecyclerView.Adapter<KidsAdapter.ViewHolder>
{
    /* Context */
    private Context mContext = null;

    /* ArrayList */
    private ArrayList<Kids> mKidsList = null;

    /* KidsAdapter */
    private KidsAdapter mKidsAdapter = this;

    /* PHBridge */
    private PHBridge mPHBridge = null;

    /* TextToSpeechClient */
    private TextToSpeechClient mTextToSpeechClient = null;

    public KidsAdapter(Context mContext, ArrayList<Kids> mKidsList, PHBridge mPHBridge, TextToSpeechClient mTextToSpeechClient)
    {
        /* Context */
        this.mContext = mContext;
        /* ArrayList */
        this.mKidsList = mKidsList;
        /* PHBridge */
        this.mPHBridge = mPHBridge;
        /* TextToSpeechClient */
        this.mTextToSpeechClient = mTextToSpeechClient;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        /* TextView */
        holder.mNameTextView.setText(mKidsList.get(position).mName);
        holder.mSortTextView.setText(String.format("(%s)", mKidsList.get(position).mSort));

        /* ImageView */
        holder.mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(mContext).load(mKidsList.get(position).mImage).error(R.drawable.img_photo).into(holder.mImageView);

        for(int count = 0, mLength = mKidsList.get(position).mColor.length; count < mLength; count++)
        {
            /* String */ final String mResult[] = mKidsList.get(position).mColor[count].split(" ");
            /* Integer */ final int mColor[] = { Integer.valueOf(mResult[0]).intValue(), Integer.valueOf(mResult[1]).intValue(), Integer.valueOf(mResult[2]).intValue() };
            /* ImageView */ holder.mColorImageView[count].setBackgroundColor(Color.rgb(mColor[0], mColor[1], mColor[2]));

            /* TextView */
            holder.mColorTextView[count].setText(String.format("#%02X%02X%02X", mColor[0], mColor[1], mColor[2]));
        }

        /* Button */
        holder.mImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /* Vibrate */
                Etc.Vibrate(mContext, new long[]{100});

                    /* SQLite DELETE Method */
                    SQLite.DeleteValue(mContext, "kids_db", "_id=?", new String[]{String.format("%d", mKidsList.get(position).mID)});

                    /* Adapter */
                    mKidsList.remove(position);
                    mKidsAdapter.notifyItemRemoved(position);
                    mKidsAdapter.notifyDataSetChanged();
            }
        });

        /* Linear Layout */
        holder.mLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /* Vibrate */
                Etc.Vibrate(mContext, new long[]{100});

                /* PHBridge */
                final List<PHLight> mLight = mPHBridge.getResourceCache().getAllLights();

                for(int count = 0, mLength = mKidsList.get(position).mColor.length; count < mLength; count++)
                {
                    /* String */ final String mResult[] = mKidsList.get(position).mColor[count].split(" ");
                    /* Integer */ final int mColor[] = { Integer.valueOf(mResult[0]).intValue(), Integer.valueOf(mResult[1]).intValue(), Integer.valueOf(mResult[2]).intValue() };

                    /* Philips Method */
                    PhilipsHueColorManager.ChangeColorHueLamp(mLight.get(count), mColor);
                }

                /* TextToSpeech */
                if(mTextToSpeechClient != null)
                {
                    if(mTextToSpeechClient.isPlaying()) { mTextToSpeechClient.stop(); }
                    else { mTextToSpeechClient.play(mKidsList.get(position).mName); }
                }
            }
        });
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_kids_adapter, parent, false); return new ViewHolder(mView); }
    @Override public int getItemCount() { return mKidsList.size(); }

    /* ViewHolder */
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        /* TextView */
        private TextView mNameTextView = null;
        private TextView mColorTextView[] = null;
        private TextView mSortTextView = null;

        /* ImageButton */
        private ImageButton mImageButton = null;

        /* ImageView */
        private ImageView mImageView = null;
        private ImageView mColorImageView[] = null;

        /* LinearLayout */
        private LinearLayout mLinearLayout = null;

        private ViewHolder(View itemView)
        {
            super(itemView);

            /* TextView */
            mNameTextView = (TextView)itemView.findViewById(R.id.CustomKidsTextView);
            mColorTextView = new TextView[]{(TextView)itemView.findViewById(R.id.CustomKidsOneColorText), (TextView)itemView.findViewById(R.id.CustomKidsTwoColorText), (TextView)itemView.findViewById(R.id.CustomKidsThreeColorText)};
            mSortTextView = (TextView)itemView.findViewById(R.id.CustomKidsSortTextView);

            /* ImageButton */
            mImageButton = (ImageButton)itemView.findViewById(R.id.CustomKidsCancel);

            /* ImageView */
            mImageView = (ImageView)itemView.findViewById(R.id.CustomKidsImageView);
            mColorImageView = new ImageView[]{(ImageView)itemView.findViewById(R.id.CustomKidsOneColor), (ImageView)itemView.findViewById(R.id.CustomKidsTwoColor), (ImageView)itemView.findViewById(R.id.CustomKidsThreeColor)};

            /* Linear Layout */
            mLinearLayout = (LinearLayout)itemView.findViewById(R.id.CustomKidsLayout);
        }
    }

    /* Kids Inner Class */
    public static class Kids
    {
        /* String */
        private String mName = null;
        private String mColor[] = null;
        private String mImage = null;
        private String mSort = null;
        /* Integer */
        private int mID = 0;

        public Kids(String mName, String[] mColor, String mImage, String mSort, int mID)
        {
            /* String */
            this.mName = mName;
            this.mColor = mColor;
            this.mSort = mSort;
            /* Integer */
            this.mImage = mImage;
            this.mID = mID;
        }
    }
}

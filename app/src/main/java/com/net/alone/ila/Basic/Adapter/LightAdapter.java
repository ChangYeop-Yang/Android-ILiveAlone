package com.net.alone.ila.Basic.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.net.alone.ila.Basic.Etc;
import com.net.alone.ila.Philips.PhilipsHueColorManager;
import com.net.alone.ila.R;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.ArrayList;

/**
 * Created by Mari on 2015-12-07.
 */
public class LightAdapter extends RecyclerView.Adapter<LightAdapter.ViewHolder>
{
    /* Context */
    private Context mContext = null;

    /* ArrayList */
    private ArrayList<Light> mArrayList = null;

    /* Integer */
    private int mColor[] = {0, 0, 0};

    public LightAdapter(Context mContext, ArrayList<Light> mArrayList)
    {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        /* PHLightState */
        final PHLightState mPHLightState = mArrayList.get(position).mPHLight.getLastKnownLightState();

        /* Connect TextView SetText */
        if(mPHLightState.isOn())
        {
            holder.mConnectTextView.setText("연결완료");
            holder.mConnectTextView.setTextColor(Color.rgb(139, 195, 74));
            /* Connect ImageView */
            holder.mConnectImageView.setImageResource(R.drawable.ic_light_green);
            holder.mConnectImageView.setTag(R.drawable.ic_light_green);
        }
        else
        {
            holder.mConnectTextView.setText("비연결");
            holder.mConnectTextView.setTextColor(Color.rgb(114, 114, 114));
            /* Connect ImageView */
            holder.mConnectImageView.setImageResource(R.drawable.ic_light_gray);
            holder.mConnectImageView.setTag(R.drawable.ic_light_gray);
        }

        /* Connect Lamp TextView SetText */
        holder.mLampTextView.setText(mArrayList.get(position).mPHLight.getName());

        /* ImageView */
        holder.mConnectImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /* Vibrate Method */
                Etc.Vibrate(mContext, new long[]{100, 200, 300});

                /* Boolean */
                final boolean mBoolean = ((ImageView)v).getTag().equals(R.drawable.ic_light_green) ? true : false;
                setPowerLampChecking(holder, position, mBoolean);
            }
        });

        /* SeekBar */
        for(int count = 0, mLength = holder.mSeekBar.length; count < mLength; count++) { setSeekBar(holder.mSeekBar[count], holder.mSeekBar, position, holder); }

        /* ImageButton */
        for(int count = 0, mLength = holder.mImageButton.length; count < mLength; count++) { setImageButton(holder.mImageButton[count], position); }
    }

    /* Setting Checking Lamp Power Method */
    private void setPowerLampChecking(ViewHolder mHolder, final int mPosition, final boolean mBoolean)
    {
        if(mBoolean)
        {
            /* Connect TextView */
            mHolder.mConnectTextView.setText("비연결");
            mHolder.mConnectTextView.setTextColor(Color.rgb(114, 114, 114));

            /* Connect ImageView */
            mHolder.mConnectImageView.setImageResource(R.drawable.ic_light_gray);
            mHolder.mConnectImageView.setTag(R.drawable.ic_light_gray);
            PhilipsHueColorManager.ChangePowerHueLamp(mArrayList.get(mPosition).mPHLight, false);
        }
        else
        {
            /* Connect TextView */
            mHolder.mConnectTextView.setText("연결완료");
            mHolder.mConnectTextView.setTextColor(Color.rgb(139, 195, 74));

            /* Connect ImageView */
            mHolder.mConnectImageView.setImageResource(R.drawable.ic_light_green);
            mHolder.mConnectImageView.setTag(R.drawable.ic_light_green);
            PhilipsHueColorManager.ChangePowerHueLamp(mArrayList.get(mPosition).mPHLight, true);
        }
    }

    /* Setting SeekBar Method */
    private void setSeekBar(SeekBar mSeekBar, SeekBar[] mSeekBarArray, final int mPosition, final ViewHolder holder)
    {
        /* SeekBar Controller */
        final PHLightState mPHLightState = mArrayList.get(mPosition).mPHLight.getLastKnownLightState();

        /* Float */
        float mFloat[] = { mPHLightState.getX(), mPHLightState.getY() };

        /* Integer */
        final int mColors = PHUtilities.colorFromXY(mFloat, mArrayList.get(mPosition).mPHLight.getModelNumber());
        /* RED */ mSeekBarArray[0].setProgress(Color.red(mColors));
        /* GREEN */ mSeekBarArray[1].setProgress(Color.green(mColors));
        /* BLUE */ mSeekBarArray[2].setProgress(Color.blue(mColors));
        /* LUX */ mSeekBarArray[3].setProgress(mArrayList.get(mPosition).mPHLight.getLastKnownLightState().getBrightness());

        /* SeekBar Listener */
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                switch (seekBar.getId())
                {
                    case (R.id.CustomLightREDSeekBar) : { mColor[0] = progress; break; }
                    case (R.id.CustomLightGREENSeekBar) : { mColor[1] = progress; break; }
                    case (R.id.CustomLightBLUESeekBar) : { mColor[2] = progress; break; }
                    case (R.id.CustomLightLUXSeekBar) : { PhilipsHueColorManager.ChangeLUXHueLamp(mArrayList.get(mPosition).mPHLight, progress); break; }
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar)
            {
                /* Philips Change Method */
                PhilipsHueColorManager.ChangeColorHueLamp(mArrayList.get(mPosition).mPHLight, mColor);
                holder.mLampTextView.setTextColor(Color.rgb(mColor[0], mColor[1], mColor[2]));
            }
        });
    }

    /* Setting ImageButton Method */
    private void setImageButton(ImageButton mImageButton, final int mPosition)
    {
        mImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /* Integer */
                int mColor[] = null;

                switch (v.getId())
                {
                    case (R.id.CustomLightQuickRED) : { mColor = new int[]{255,0,0}; break; }
                    case (R.id.CustomLightQuickBLUE) : { mColor = new int[]{0,0,255}; break; }
                    case (R.id.CustomLightQuickGREEN) : { mColor = new int[]{0,255,0}; break; }
                    case (R.id.CustomLightQuickYellow) : { mColor = new int[]{255,228,0}; break; }
                    case (R.id.CustomLightQuickOrange) : { mColor = new int[]{255,187,0}; break; }
                    case (R.id.CustomLightQuickPurple) : { mColor = new int[]{95,0,255}; break; }
                    case (R.id.CustomLightQuickPink) : { mColor = new int[]{255,0,221}; break; }
                    case (R.id.CustomLightQuickBrown) : { mColor = new int[]{102,75,0}; break; }
                }

                /* Vibrate */
                Etc.Vibrate(mContext, new long[]{100, 200, 300});

                /* PhilipsHueColorManager */
                PhilipsHueColorManager.ChangeColorHueLamp(mArrayList.get(mPosition).mPHLight, mColor);
            }
        });
    }


    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_light_adapter, parent, false); return new ViewHolder(mView); }
    @Override public int getItemCount() { return mArrayList.size(); }

    /* ViewHolder */
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        /* TextView */
        private TextView mConnectTextView = null;
        private TextView mLampTextView = null;

        /* ImageView */
        private ImageView mConnectImageView = null;

        /* SeekBar */
        private SeekBar mSeekBar[] = null;

        /* ImageButton */
        private ImageButton mImageButton[] = null;

        private ViewHolder(View itemView)
        {
            super(itemView);

            /* TextView */
            mConnectTextView = (TextView)itemView.findViewById(R.id.CustomLightStateText);
            mLampTextView = (TextView)itemView.findViewById(R.id.CustomLightName);

            /* ImageView */
            mConnectImageView = (ImageView)itemView.findViewById(R.id.CustomLightImageView);

            /* SeekBar */
            mSeekBar = new SeekBar[]{(SeekBar)itemView.findViewById(R.id.CustomLightREDSeekBar), (SeekBar)itemView.findViewById(R.id.CustomLightGREENSeekBar), (SeekBar)itemView.findViewById(R.id.CustomLightBLUESeekBar), (SeekBar)itemView.findViewById(R.id.CustomLightLUXSeekBar)};

            /* ImageButton */
            mImageButton = new ImageButton[]{(ImageButton)itemView.findViewById(R.id.CustomLightQuickRED), (ImageButton)itemView.findViewById(R.id.CustomLightQuickBLUE), (ImageButton)itemView.findViewById(R.id.CustomLightQuickOrange),
                    (ImageButton)itemView.findViewById(R.id.CustomLightQuickBrown), (ImageButton)itemView.findViewById(R.id.CustomLightQuickYellow), (ImageButton)itemView.findViewById(R.id.CustomLightQuickGREEN), (ImageButton)itemView.findViewById(R.id.CustomLightQuickPink),
                    (ImageButton)itemView.findViewById(R.id.CustomLightQuickPurple)};
        }
    }

    /* Light Class */
    public static class Light
    {
        /* PHLight */
        private PHLight mPHLight = null;
        public Light(PHLight mPHLight) { this.mPHLight = mPHLight; }
    }
}

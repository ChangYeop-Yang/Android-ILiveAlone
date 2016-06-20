package com.net.alone.ila;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.net.alone.ila.Basic.Adapter.LightAdapter;
import com.net.alone.ila.Basic.Etc;
import com.net.alone.ila.Philips.PhilipsHueColorManager;
import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mari on 2015-11-24.
 */
public class LightActivity extends BaseActivity
{
    /* Context */
    private Context mContext = null;

    /* Boolean */
    private boolean mBoolean = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        /* Context */
        mContext = LightActivity.this;

        /* Light ToolBar */
        setToolbar("조명", null);

        /* RecyclerView Method */
        setRecyclerView((RecyclerView) findViewById(R.id.LightRecyclerView));

        /* SeekBar */
        final SeekBar mSeekBar[] = {(SeekBar)findViewById(R.id.LightREDSeekBar), (SeekBar)findViewById(R.id.LightGREENSeekBar), (SeekBar)findViewById(R.id.LightBLUESeekBar), (SeekBar)findViewById(R.id.LightLUXSeekBar)};
        for(int count = 0, mLength = mSeekBar.length; count < mLength; count++) { setSeekBar(mSeekBar[count], mSeekBar); }

        /* Switch */
        final ImageButton mImageButton[] = {(ImageButton)findViewById(R.id.LightREDPencil), (ImageButton)findViewById(R.id.LightBLUEPencil), (ImageButton)findViewById(R.id.LightGREENPencil), (ImageButton)findViewById(R.id.LightPurplePencil),
                (ImageButton)findViewById(R.id.LightORANGEPencil), (ImageButton)findViewById(R.id.LightYellowPencil), (ImageButton)findViewById(R.id.LightPowerSwitch) };
        for(int count = 0, mLength = mImageButton.length; count < mLength; count++) { setImageButton(mImageButton[count]); }
    }

    /* SetRecyclerView Method */
    private void setRecyclerView(RecyclerView mRecyclerView)
    {
        /* RecyclerView Init */
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /* ArrayList */
        ArrayList<LightAdapter.Light> mArrayList = new ArrayList<LightAdapter.Light>(10);

        /* PHBridge */
        final List<PHLight> mList = mPHBridge.getResourceCache().getAllLights();
        for(int count = 0, mLength = mList.size(); count < mLength; count++) { mArrayList.add(new LightAdapter.Light(mList.get(count))); }

        /* ToolBar */
        mToolbar.setSubtitle(String.format("%d 개", mList.size()));

        /* RecyclerView Adapter */
        mRecyclerView.setAdapter(new LightAdapter(mContext, mArrayList));

        /* DisplayMetrics */
        final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        /* Height */
        int mHeight = (mDisplayMetrics.heightPixels * 90) / 100;
        mRecyclerView.getLayoutParams().height = mHeight;
    }

    /* Set SeekBar Method */
    private void setSeekBar(SeekBar mSeekBar, final SeekBar[] mSeekBarArray)
    {
        /* SeekBar Listener */
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (seekBar.getId()) {
                    case (R.id.LightREDSeekBar): {
                        PhilipsHueColorManager.ChangeALLHueLampColorLamp(new int[]{progress, mSeekBarArray[1].getProgress(), mSeekBarArray[2].getProgress()});
                        break;
                    }
                    case (R.id.LightBLUESeekBar): {
                        PhilipsHueColorManager.ChangeALLHueLampColorLamp(new int[]{mSeekBarArray[0].getProgress(), mSeekBarArray[1].getProgress(), progress});
                        break;
                    }
                    case (R.id.LightGREENSeekBar): {
                        PhilipsHueColorManager.ChangeALLHueLampColorLamp(new int[]{mSeekBarArray[0].getProgress(), progress, mSeekBarArray[2].getProgress()});
                        break;
                    }
                    case (R.id.LightLUXSeekBar): {
                        PhilipsHueColorManager.ChangeALLHueLUXLamp(progress);
                        break;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /* Set Switch Method */
    private void setImageButton(ImageButton mImageButton)
    {
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case (R.id.LightPowerSwitch):
                    {
                        /* Boolean */
                        mBoolean = mBoolean ? false : true;
                        /* Vibrate */
                        Etc.Vibrate(mContext, new long[]{100, 200, 300});
                        /* PhilipsHueColorManager */
                        PhilipsHueColorManager.ChangeALLPowerHueLamp(mBoolean); break;
                    }
                    case (R.id.LightREDPencil) : { PhilipsHueColorManager.ChangeALLHueLampColorLamp(new int[]{255, 0, 0}); break; }
                    case (R.id.LightBLUEPencil) : { PhilipsHueColorManager.ChangeALLHueLampColorLamp(new int[]{0, 0, 255}); break; }
                    case (R.id.LightGREENPencil) : { PhilipsHueColorManager.ChangeALLHueLampColorLamp(new int[]{0, 255, 0}); break; }
                    case (R.id.LightPurplePencil) : { PhilipsHueColorManager.ChangeALLHueLampColorLamp(new int[]{95, 0, 255}); break; }
                    case (R.id.LightORANGEPencil) : { PhilipsHueColorManager.ChangeALLHueLampColorLamp(new int[]{255, 187, 0}); break; }
                    case (R.id.LightYellowPencil) : { PhilipsHueColorManager.ChangeALLHueLampColorLamp(new int[]{255, 228, 0}); break; }
                }

            }
        });
    }
}

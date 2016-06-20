package com.net.alone.ila;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.net.alone.ila.Basic.Adapter.LifeSaveAdapter;
import com.net.alone.ila.Basic.SQLite;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mari on 2015-12-09.
 */
public class LifeSaveActivity extends BaseActivity
{
    /* Context */
    private Context mContext = null;

    /* Integer */
    private int mColor[] = {0, 0, 0};
    private int mLUX = 0;

    /* Bitmap */
    private Bitmap mBitmap = null;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_save);

        /* ToolBar Method */
        setToolbar("나만의 색깔 만들기", null);

        /* Context */
        mContext = LifeSaveActivity.this;

        /* SeekBar */
        final SeekBar mSeekBar[] = {(SeekBar)findViewById(R.id.QuickREDSeekBar), (SeekBar)findViewById(R.id.QuickBLUESeekBar), (SeekBar)findViewById(R.id.QuickGREENSeekBar), (SeekBar)findViewById(R.id.QuickLUXSeekBar)};
        for(int count = 0, mLength = mSeekBar.length; count < mLength; count++) { setSeekBar(mSeekBar[count]); }
    }

    /* Set RecyclerView Method */
    private void setRecyclerView(RecyclerView mRecyclerView)
    {
        /* RecyclerView Init */
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /* View */
        ((View)findViewById(R.id.KidsSavenformation)).setVisibility(View.GONE);

        /* SeekBar */
        final SeekBar mSeekBar[] = {(SeekBar)findViewById(R.id.QuickREDSeekBar), (SeekBar)findViewById(R.id.QuickBLUESeekBar), (SeekBar)findViewById(R.id.QuickGREENSeekBar), (SeekBar)findViewById(R.id.QuickLUXSeekBar)};

        /* Palette */
        final Palette mPalette = Palette.from(mBitmap).generate();

        /* ArrayList */
        final ArrayList<LifeSaveAdapter.LifeColor> mArrayList = new ArrayList<LifeSaveAdapter.LifeColor>(10);
        for(Palette.Swatch mSwatch : mPalette.getSwatches()) { mArrayList.add(new LifeSaveAdapter.LifeColor(mSwatch)); }
        mRecyclerView.setAdapter(new LifeSaveAdapter(mContext, mArrayList, mSeekBar, (TextView) findViewById(R.id.LifeSaveColorCodeText)));
    }

    /* Set SeekBar Method */
    private void setSeekBar(SeekBar mSeekBar)
    {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                switch (seekBar.getId())
                {
                    case (R.id.QuickREDSeekBar): { mColor[0] = progress; break; }
                    case (R.id.QuickBLUESeekBar): { mColor[1] = progress; break; }
                    case (R.id.QuickGREENSeekBar): { mColor[2] = progress; break; }
                    case (R.id.QuickLUXSeekBar): { mLUX = progress; break; }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                /* TextView */
                final TextView mTextView = (TextView) findViewById(R.id.LifeSaveColorCodeText);
                mTextView.setText(String.format("#%02X%02X%02X", mColor[0], mColor[1], mColor[2]));
                mTextView.setTextColor(Color.rgb(mColor[0], mColor[1], mColor[2]));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    { getMenuInflater().inflate(R.menu.menu_life_save, menu); return true; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /* ActionBar Item */
        switch (item.getItemId())
        {
            case (R.id.CustomSave) :
            {
                /* String */
                final String mString = ((AppCompatEditText)findViewById(R.id.LifeSaveEditText)).getText().toString();

                /* ContentValues */
                final ContentValues mContentValues = new ContentValues();

                /* Color */
                mContentValues.put("R", mColor[0]); mContentValues.put("G", mColor[1]);
                mContentValues.put("B", mColor[2]); mContentValues.put("L", mLUX);

                /* Name */
                mContentValues.put("name", (mString.equals("") ? "이름없음" : mString));

                /* SQLite Insert Method */
                SQLite.InsertValue(mContext, "life_db", mContentValues);

                finish(); return false;
            }
            case (R.id.CustomCamera) :
            {
                /* AlertDialog */
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setIcon(R.drawable.ic_camera_black).setTitle("사진을 통한 색깔 설정").setMessage("● 촬영 : 카메라를 통하여 사진을 촬영 한 후 촬영 된 사진의 모든 색상을 가져옵니다.\n\n● 앨범 : 사진이 저장 된 앨범을 통하여 사진을 가져 온 뒤에 사진의 모든 색상을 가져옵니다.\n");
                mBuilder.setPositiveButton("촬영", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "촬영하기", Toast.LENGTH_SHORT).show();
                        startActivityForResult(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_CAPTURE);
                    }
                }).setNegativeButton("앨범", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "앨범에서 가져오기", Toast.LENGTH_SHORT).show();
                        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_CODE_IMAGE);
                    }
                }).show();
                return false;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK)
        {
            try
            {
                /* Bitmap */
                mBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                /* RecyclerView Method */
                setRecyclerView((RecyclerView)findViewById(R.id.LifeSaveRecyclerView));
            }
            catch (IOException ex) { ex.printStackTrace(); }
        }
    }
}

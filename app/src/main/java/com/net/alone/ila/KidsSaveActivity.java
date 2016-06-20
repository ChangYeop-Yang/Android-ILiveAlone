package com.net.alone.ila;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.net.alone.ila.Basic.Adapter.KidsSaveAdapter;
import com.net.alone.ila.Basic.Etc;
import com.net.alone.ila.Basic.SQLite;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Mari on 2015-12-13.
 */
public class KidsSaveActivity extends BaseActivity implements View.OnClickListener
{
    /* Context */
    private Context mContext = null;

    /* String */
    private String mPath = null;
    private String mSort = null;

    /* ArrayList */
    public static ArrayList<String> mColorList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kids_save);

        /* Context */
        mContext = KidsSaveActivity.this;

        /* ToolBar */
        setToolbar("색깔 공부 만들기", null);

        /* ArrayList */
        mColorList = new ArrayList<String>(3);

        /* ImageView */
        ((ImageView)findViewById(R.id.KidsSaveImageView)).setOnClickListener(this);

        /* RadioGroup */
        setRadioGroup((RadioGroup)findViewById(R.id.KidsSaveSortRadio));
    }

    /* Set RecyclerView Method */
    private void setRecyclerView(RecyclerView mRecyclerView, Bitmap mBitmap)
    {
        /* RecyclerView Init */
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /* View */
        ((View)findViewById(R.id.KidsSavenformation)).setVisibility(View.GONE);

        /* ArrayList */
        final ArrayList<KidsSaveAdapter.KidsColor> mColorList = new ArrayList<KidsSaveAdapter.KidsColor>(10);

        /* Palette */
        final Palette mPalette = Palette.from(mBitmap).generate();
        for(Palette.Swatch mSwatch : mPalette.getSwatches()) { mColorList.add(new KidsSaveAdapter.KidsColor(mSwatch)); }

        /* Adapter */
        mRecyclerView.setAdapter(new KidsSaveAdapter(mContext, mColorList));
    }

    /* Set RadioGroup Method */
    private void setRadioGroup(final RadioGroup mRadioGroup)
    {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            { Toast.makeText(mContext, (mSort = ((RadioButton)findViewById(checkedId)).getText().toString()), Toast.LENGTH_SHORT).show(); }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    { getMenuInflater().inflate(R.menu.menu_save, menu); return true; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Etc.Vibrate(mContext, new long[]{100});
        /* ActionBar Item */
        switch (item.getItemId())
        {
            /* Alarm Save Button */
            case (R.id.CustomSave) :
            {
                if(mColorList.size() < 3) { Toast.makeText(mContext, "색상을 3가지 선택해주세요.", Toast.LENGTH_SHORT).show(); }
                else if(mSort.equals("")) { Toast.makeText(mContext, "분류를 선택해주세요.", Toast.LENGTH_SHORT).show(); }
                else
                {
                    /* Content Value */
                    final ContentValues mContentValues = new ContentValues();
                    /* Name */
                    final String mTitle = ((AppCompatEditText)findViewById(R.id.KidsSaveEditText)).getText().toString();
                    mContentValues.put("name", (mTitle.equals("") ? "이름 없음" : mTitle) );
                    /* Color */
                    mContentValues.put("color1", mColorList.get(0));
                    mContentValues.put("color2", mColorList.get(1));
                    mContentValues.put("color3", mColorList.get(2));
                    /* Image */
                    mContentValues.put("img", mPath);
                    /* Sort */
                    mContentValues.put("sort", mSort);
                    /* SQLite Insert Method */
                    SQLite.InsertValue(mContext, "kids_db", mContentValues);
                    finish(); return false;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK)
        {
            /* Glide */
            Glide.with(mContext).load(data.getData()).error(R.drawable.img_palette).into((ImageView)findViewById(R.id.KidsSaveImageView));
            ((ImageView)findViewById(R.id.KidsSaveImageView)).setScaleType(ImageView.ScaleType.FIT_XY);

            /* String */
            final Cursor mCursor = getContentResolver().query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            mCursor.moveToFirst();
            mPath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            mCursor.close();

            /* RecyclerView Method */
            try { setRecyclerView((RecyclerView)findViewById(R.id.KidsSaveColorRecyclerView), BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()))); }
            catch (FileNotFoundException e) { e.printStackTrace(); }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            /* Camera ImageView */
            case (R.id.KidsSaveImageView) :
            {
                /* AlertDialog */
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setIcon(R.drawable.ic_camera_black).setTitle("사진 촬영 및 가져오기").setMessage("● 촬영 : 카메라를 통하여 사진을 촬영 한 후 촬영 된 사진의 모든 색상을 가져옵니다.\n\n● 앨범 : 사진이 저장 된 앨범을 통하여 사진을 가져 온 뒤에 사진의 모든 색상을 가져옵니다.\n");
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
                break;
            }
        }
    }
}

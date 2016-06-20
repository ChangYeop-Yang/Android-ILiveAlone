package com.net.alone.ila;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.net.alone.ila.Basic.Adapter.KidsAdapter;
import com.net.alone.ila.Basic.SQLite;

import net.daum.mf.speech.api.TextToSpeechClient;
import net.daum.mf.speech.api.TextToSpeechListener;
import net.daum.mf.speech.api.TextToSpeechManager;

import java.util.ArrayList;

/**
 * Created by Mari on 2015-12-13.
 */
public class KidsActivity extends BaseActivity implements View.OnClickListener
{
    /* Context */
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kids);

        /* ToolBar */
        setToolbar("키즈화면", null);

        /* Context */
        mContext = KidsActivity.this;

        /* Floating Button */
        ((FloatingActionButton)findViewById(R.id.KidsFloatingButton)).setOnClickListener(this);

        /* RecyclerView */
        setRecyclerView((RecyclerView)findViewById(R.id.KidsRecyclerView), "SELECT * FROM kids_db");
     }

    /* Set RecyclerView Method */
    private void setRecyclerView(RecyclerView mRecyclerView, String mQuery)
    {
        /* RecyclerView Init */
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /* ArrayList */
        final ArrayList<KidsAdapter.Kids> mArrayList = new ArrayList<KidsAdapter.Kids>(10);

        /* Cursor */
        Cursor mCursor = null;
        try
        {
            mCursor = SQLite.SelectValue(mContext, mQuery);
            while (mCursor.moveToNext())
            { mArrayList.add(new KidsAdapter.Kids(mCursor.getString(1), new String[]{mCursor.getString(2), mCursor.getString(3), mCursor.getString(4)}, mCursor.getString(5), mCursor.getString(6), mCursor.getInt(0))); }

            /* ToolBar */
            mToolbar.setSubtitle(String.format("%d 개", mCursor.getCount()));

            /* RecyclerView */
            mRecyclerView.setAdapter(new KidsAdapter(mContext, mArrayList, mPHBridge, createTextToSpeech()));
        }
        catch (SQLiteException ex) { ex.printStackTrace(); }
        finally { mCursor.close(); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    { getMenuInflater().inflate(R.menu.menu_kids, menu); return true; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case (R.id.KidsNameSort) : { setRecyclerView((RecyclerView) findViewById(R.id.KidsRecyclerView), "SELECT * FROM kids_db ORDER BY name ASC"); return false; }
            case (R.id.KidsAnimalSort) : { setRecyclerView((RecyclerView) findViewById(R.id.KidsRecyclerView), "SELECT * FROM kids_db WHERE sort='동물' ORDER BY sort ASC"); return false; }
            case (R.id.KidsFruitsSort) : { setRecyclerView((RecyclerView) findViewById(R.id.KidsRecyclerView), "SELECT * FROM kids_db WHERE sort='과일' ORDER BY sort ASC"); return false; }
            case (R.id.KidsVegetableSort) : { setRecyclerView((RecyclerView) findViewById(R.id.KidsRecyclerView), "SELECT * FROM kids_db WHERE sort='채소' ORDER BY sort ASC"); return false; }
            case (R.id.KidsObjectSort) : { setRecyclerView((RecyclerView) findViewById(R.id.KidsRecyclerView), "SELECT * FROM kids_db WHERE sort='사물' ORDER BY sort ASC"); return false; }
        }

        return super.onOptionsItemSelected(item);
    }

    /* TextToSpeech Method */
    private TextToSpeechClient createTextToSpeech()
    {
        /* TextToSpeechManager */
        TextToSpeechManager.getInstance().initializeLibrary(mContext);
        TextToSpeechClient mTextToSpeechClient = new TextToSpeechClient.Builder().setApiKey("c9fa40442657ba649f3fa8157919609d").setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)
                .setSpeechSpeed(1.0).setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_DIALOG_BRIGHT).setListener(new TextToSpeechListener() {
                    @Override public void onFinished() {}

                    @Override
                    public void onError(int i, String s) {
                        /* String */
                        String mString = null;
                        switch (i) {
                            case (TextToSpeechClient.ERROR_NETWORK): {}
                            case (TextToSpeechClient.ERROR_NETWORK_TIMEOUT): { mString = "네트워크 상태를 확인해주세요."; break; }
                            case (TextToSpeechClient.ERROR_SERVER_AUTHENTICATION): { mString = "APIKEY 인증 오류 발생"; TextToSpeechManager.getInstance().finalizeLibrary(); break; }
                            case (TextToSpeechClient.ERROR_SERVER_SPEECH_TEXT_BAD): { mString = "특수문자 사용불가"; break; }
                            case (TextToSpeechClient.ERROR_SERVER_SPEECH_TEXT_FORBIDDEN): { mString = "서비스 사용 최대 허용 횟수 초과"; break; }
                        }
                        /* Toast */
                        Log.e("TextToSpeech TAG", s);
                        Toast.makeText(mContext, mString, Toast.LENGTH_SHORT).show();
                    }
                }).build();

        return mTextToSpeechClient;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            /* Floating Action Button */
            case (R.id.KidsFloatingButton) : { startActivity(new Intent(mContext, KidsSaveActivity.class)); break; }
        }
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        setRecyclerView((RecyclerView) findViewById(R.id.KidsRecyclerView), "SELECT * FROM kids_db");
    }
}

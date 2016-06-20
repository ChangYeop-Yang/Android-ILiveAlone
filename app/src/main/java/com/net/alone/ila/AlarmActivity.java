package com.net.alone.ila;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.net.alone.ila.Basic.Adapter.AlarmAdapter;
import com.net.alone.ila.Basic.SQLite;

import java.util.ArrayList;

/**
 * Created by Mari on 2015-11-24.
 */
public class AlarmActivity extends BaseActivity implements View.OnClickListener
{
    /* Context */
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        /* ToolBar */
        setToolbar("알람 화면", null);

        /* Context */
        mContext = AlarmActivity.this;

        /* RecyclerView */
        setRecyclerView((RecyclerView)findViewById(R.id.AlarmRecyclerView), "SELECT * FROM alarm_db");

        /* FloatingButton */
        final FloatingActionButton mFloatingActionButton = (FloatingActionButton)findViewById(R.id.AlarmFloatingButton);
        mFloatingActionButton.setOnClickListener(this);
    }

    /* SetRecyclerView Method */
    private void setRecyclerView(RecyclerView mRecyclerView, String mQuery)
    {
        /* RecyclerView Init */
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /* RecyclerView Insert */
        final ArrayList<AlarmAdapter.Alarm> mArrayList = new ArrayList<AlarmAdapter.Alarm>(10);

        /* Cursor */
        final Cursor mCursor = SQLite.SelectValue(mContext, mQuery);
        try
        {
            /* ToolBar */
            mToolbar.setSubtitle(String.format("%d 건", mCursor.getCount()));

            while(mCursor.moveToNext())
            {
                /* Integer */
                int mSchedule[] = new int[]{mCursor.getInt(6), mCursor.getInt(7), mCursor.getInt(8), mCursor.getInt(9), mCursor.getInt(10), mCursor.getInt(11), mCursor.getInt(12)};
                int mColor[] = new int[]{mCursor.getInt(13), mCursor.getInt(14), mCursor.getInt(15)};

                /* AlarmAdapter */
                mArrayList.add(new AlarmAdapter.Alarm(mCursor.getString(1), mCursor.getInt(0), mCursor.getInt(2), mCursor.getInt(3), mCursor.getInt(4), mCursor.getInt(5), mSchedule, mColor, mCursor.getInt(16)));
            }

            /* RecyclerView Adapter */
            mRecyclerView.setAdapter(new AlarmAdapter(mContext, mArrayList));
        }
        catch(CursorIndexOutOfBoundsException ex) { ex.printStackTrace(); }
        finally { mCursor.close(); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    { getMenuInflater().inflate(R.menu.menu_alarm, menu); return true; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case (R.id.AlarmTextSort) : { setRecyclerView((RecyclerView)findViewById(R.id.AlarmRecyclerView), "SELECT * FROM alarm_db ORDER BY name ASC"); return false; }
            case (R.id.AlarmTimeSort) : { setRecyclerView((RecyclerView)findViewById(R.id.AlarmRecyclerView), "SELECT * FROM alarm_db ORDER BY hour, minute ASC"); return false; }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            /* Floating Button */
            case (R.id.AlarmFloatingButton) : { startActivity(new Intent(mContext, AlarmSaveActivity.class)); break; }
        }
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        /* RecyclerView */
        setRecyclerView((RecyclerView)findViewById(R.id.AlarmRecyclerView), "SELECT * FROM alarm_db");
    }
}

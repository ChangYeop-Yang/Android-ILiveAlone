package com.net.alone.ila;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.widget.TimePicker;
import android.widget.Toast;

import com.net.alone.ila.Calendars.Calendars;
import com.net.alone.ila.Philips.PhilipsHueScheduleManager;
import com.net.alone.ila.Schedule.ScheduleManager;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Mari on 2015-12-12.
 */
public class SettingActivity extends PreferenceActivity
{
    /* Context */
    private Context mContext = null;

    /* String */
    private static final String SCHEDULE_ON_ID = "GoOutOn";
    private static final String SCHEDULE_OFF_ID = "GoOutOff";

    /* GregorianCalendar */
    private GregorianCalendar mGregorianCalendar = null;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_setting);

        /* Context */
        mContext = SettingActivity.this;

        /* GregorianCalendar */
        mGregorianCalendar = new GregorianCalendar();

        /* Preference */
        final Preference mPreference[] = {(Preference) findPreference("GoOutStart"), (Preference)findPreference("GoOutEnd")};
        for(int count = 0, mLength = mPreference.length; count < mLength; count++) { setPreference(mPreference[count]); }

        /* SwitchPreference */
        final SwitchPreference mSwitchPreference[] = {(SwitchPreference)findPreference("GoOut"), (SwitchPreference)findPreference("Today"), (SwitchPreference)findPreference("Calendar"), (SwitchPreference)findPreference("Weather")};
        for(int count = 0, mLength = mSwitchPreference.length; count < mLength; count++) { setSwitchPreference(mSwitchPreference[count]); }
    }

    /* TimePickerDialog.OnTimeSetListener */
    private TimePickerDialog.OnTimeSetListener setTimePickerDialog(final Preference mPreference)
    {
        return new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                switch (mPreference.getKey())
                {
                    case ("GoOutStart") : { PhilipsHueScheduleManager.CreateHueOnSchedule(hourOfDay, minute, SCHEDULE_ON_ID); break; }
                    case ("GoOutEnd") : { PhilipsHueScheduleManager.CreateHueOffSchedule(hourOfDay, minute, SCHEDULE_OFF_ID); break; }
                    case ("Weather") : { ScheduleManager.WeatherAlarm(mContext, 200, hourOfDay, minute); mPreference.getEditor().putInt("WeatherHour", hourOfDay).putInt("WeatherMinute", minute).commit(); break; }
                    case ("Today") : { ScheduleManager.CreateAlarm(mContext, "오늘의 색상", 8278, hourOfDay, minute, new int[]{1, 1, 1, 1, 1, 1, 1}, new int[]{(int)Math.random()*255, (int)Math.random()*255, (int)Math.random()*255}, true); break; }
                }
                /* Toast */
                Toast.makeText(mContext, String.format("%s %d시 %d분 설정", mPreference.getTitle(), hourOfDay, minute), Toast.LENGTH_SHORT).show();
            }
        };
    }

    /* Setting Preference Method */
    private void setPreference(Preference mPreference)
    {
        /* setOnPreferenceClickListener */
        mPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(final Preference preference)
            {
                /* TimePickerDialog */
                final TimePickerDialog mTimePickerDialog = new TimePickerDialog(mContext, setTimePickerDialog(preference), mGregorianCalendar.get(Calendar.HOUR_OF_DAY), mGregorianCalendar.get(Calendar.MINUTE), false);

                 /* GoOut Mode */
                if(((SwitchPreference)findPreference("GoOut")).isChecked())
                {
                    switch (preference.getKey())
                    {
                        /* Schedule Start Time Preference */
                        case ("GoOutStart") : { mTimePickerDialog.show(); break; }
                        /* Schedule End Time Preference */
                        case ("GoOutEnd") : { mTimePickerDialog.show(); break; }
                    }
                }

                return false;
            }
        });
    }

    /* Setting Switch Preference Method */
    private void setSwitchPreference(SwitchPreference mSwitchPreference)
    {
        mSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(final Preference preference, Object newValue)
            {
                /* Preference */
                ((SwitchPreference)preference).setChecked((boolean)newValue);
                preference.getEditor().putBoolean(preference.getKey(), (boolean) newValue).commit();

                switch (preference.getKey())
                {
                    /* 외출 기능 */
                    case ("GoOut") :
                    {
                        /* Enabled */
                        ((Preference)findPreference("GoOutStart")).setEnabled((boolean) newValue);
                        ((Preference)findPreference("GoOutEnd")).setEnabled((boolean) newValue);

                        /* Switch Off */
                        if(!(boolean)newValue) { PhilipsHueScheduleManager.DeleteHueSchedule(); } break;
                    }
                    /* 오늘의 색상 기능 */
                    case ("Today") :
                    {
                        /* Switch Off */
                        if((boolean)newValue) { new TimePickerDialog(mContext, setTimePickerDialog(preference), mGregorianCalendar.get(Calendar.HOUR_OF_DAY), mGregorianCalendar.get(Calendar.MINUTE), false).show(); }
                        else { ScheduleManager.DeleteAlarmManager(mContext, 8278); } break;
                    }
                    /* 캘린더 동기화 기능 */
                    case ("Calendar") :
                    {
                        if((boolean)newValue)
                        {
                            /* AlertDialog */
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                            mBuilder.setTitle("캘린더 정보 연동 동의").setIcon(R.drawable.ic_date).setMessage(getString(R.string.SettingCalendarInformation));
                            mBuilder.setPositiveButton("동의", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) { Calendars.ImportCalendars(mContext, true); }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); ((SwitchPreference)preference).setChecked(false); }
                            }).show();
                        } else { Calendars.ImportCalendars(mContext, false); }
                        break;
                    }
                    /* 날씨 기능 */
                    case ("Weather") :
                    {
                        if((boolean)newValue) { new TimePickerDialog(mContext, setTimePickerDialog(preference), mGregorianCalendar.get(Calendar.HOUR_OF_DAY), mGregorianCalendar.get(Calendar.MINUTE), false).show(); }
                        else { ScheduleManager.DeleteAlarmManager(mContext, 200); }
                        break;
                    }
                }

                /* Toast */
                Toast.makeText(mContext, String.format("%s %s", preference.getTitle(), ((boolean) newValue ? "활성화" : "비활성화")), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}
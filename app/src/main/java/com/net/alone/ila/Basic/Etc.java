package com.net.alone.ila.Basic;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Vibrator;

/**
 * Created by Mari on 2015-12-06.
 */
public class Etc
{
    /* Vibrate Method */
    public static void Vibrate(Context mContext, long[] mVibrate)
    {
        /* Vibrator */
        final Vibrator mVibrator = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(300); mVibrator.vibrate(mVibrate, -1);
    }

    /* SoundMeter Class */
    public static class SoundMeter
    {
        /* Context */
        private Context mContext = null;

        /* AudioRecord */
        private AudioRecord mAudioRecord = null;

        /* Integer */
        private int minSize = 0;

        public SoundMeter(Context mContext) { this.mContext = mContext; }

        /* Start Audio Method */
        public void start()
        {
            /* AudioRecord */
            minSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize);
            mAudioRecord.startRecording();
        }

        /* Stop Audio Method */
        public void stop()
        {
            if(mAudioRecord != null)
            {
                /* AudioRecord */
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }
        }

        /* Amplitude Method */
        public double getAmplitude()
        {

            /* Short */
            short[] mBuffer = new short[minSize];
            mAudioRecord.read(mBuffer, 0, minSize);

            /* Integer */
            int max = 0;
            for(short mShort : mBuffer) { if(Math.abs(mShort) > max) { max = Math.abs(mShort); } }
            return max;
        }
    }
}
package com.net.alone.ila.Basic;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.net.alone.ila.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Mari on 2015-11-25.
 */
public class Weather extends AsyncTask<Void, Void, ArrayList<String>>
{
    /* Context */
    private Context mContext = null;

    /* Integer */
    private int mGridX = 0, mGridY = 0;

    /* String */
    private String mURL = null;
    private final String mTag[] = {"temp", "wfKor", "pop", "wdKor"};

    /* ArrayLIst */
    private ArrayList<String> mArrayList = null;

    /* TextView */
    private TextView mTextView[] = null;

    /* ImageView */
    private ImageView mImageView = null;

    /* Boolean */
    private Boolean mBoolean = null;

    public Weather(Context mContext, double mMapX, double mMapY, TextView[] mTextView, ImageView mImageView)
    {
        /* Context */
        this.mContext = mContext;
        /* Double */
        changeDFSXY(mMapX, mMapY);
        /* TextView */
        this.mTextView = mTextView;
        /* ImageView */
        this.mImageView = mImageView;
        /* Boolean */
        mBoolean = true;
    }
    public Weather(Context mContext, double mMapX, double mMapY)
    {
        /* Context */
        this.mContext = mContext;
        /* Double */
        changeDFSXY(mMapX, mMapY);
        /* Boolean */
        mBoolean = false;
    }

    @Override
    protected void onPreExecute()
    {
        /* ArrayList */
        mArrayList = new ArrayList<String>(10);
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params)
    {
        try
        {
			/* Xml pull 파실 객체 생성 */
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

			/* 외부 사이트 연결 관련 구문 */
            URL url = new URL(mURL); /* URL 객체 생성 */
            InputStream in = url.openStream(); /* 해당 URL로 연결 */
            parser.setInput(in, "UTF-8"); /* 외부 사이트 데이터와 인코딩 방식을 설정 */

		    /* XML 파싱 관련 변수 관련 구문 */
            int eventType = parser.getEventType(); /* 파싱 이벤트  관련 저장 변수 생성 */
            boolean isItemTag = false;
            String tagName = null; /* Tag의 이름을 저장 하는 변수 생성 */

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                if(eventType == XmlPullParser.START_TAG)
                {
                    tagName = parser.getName();

                    for (int count = 0, mTagLength = mTag.length; count < mTagLength; count++)
                    { if (tagName.equals(mTag[count])) { mArrayList.add(parser.nextText()); } }
                }
                else if (eventType == XmlPullParser.END_TAG) { tagName = parser.getName();  }
                eventType = parser.next(); /* 다음 XML 객체로 이동 */
            }
        }
        catch (Exception e)
        { Log.e("KMAXmlParser", "KMA Error."); }
        return mArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result)
    {
        if(mBoolean)
        {
            /* TextView */
            mTextView[0].setText(mArrayList.get(0) + " ℃"); /* 온도 */
            mTextView[1].setText(mArrayList.get(1)); /* 날씨상태 */
            mTextView[2].setText(mArrayList.get(2) + " %"); /* 강수확률 */
            mTextView[3].setText(mArrayList.get(3)); /* 바람방향 */

            /* ImageView */
            setIconWeather(mContext, mArrayList.get(1), mImageView);
        }

        super.onPostExecute(result);
    }

    /* IconMatch Method */
    public static int[] setIconWeather(Context mContext, String mString, ImageView mImageView)
    {
        /* Integer */
        final int mWeatherColor[][] = { {255,152,0}, {25,118,114}, {69,90,100}, {0,121,107}, {255,255,255} };
        int mWeatherState[] = {0, 0};
        switch (mString)
        {
            /* 맑음 */
            case ("맑음") :
            {
                /* Hue Color Value */ mWeatherState[0] = 0;
                /* Weather ICon Value */ mWeatherState[1] = R.drawable.ic_clear; break;
            }
            /* 구름 */
            case ("구름 조금") : {}
            case ("구름 많음") :
            {
                /* Hue Color Value */ mWeatherState[0] = 1;
                /* Weather ICon Value */ mWeatherState[1] = R.drawable.ic_partly; break;
            }
            case ("흐림") :
            {
                /* Hue Color Value */ mWeatherState[0] = 2;
                /* Weather ICon Value */ mWeatherState[1] = R.drawable.ic_cloudy; break;
            }
            case ("비") :
            {
                /* Hue Color Value */ mWeatherState[0] = 3;
                /* Weather ICon Value */ mWeatherState[1] = R.drawable.ic_rain; break;
            }
            case ("눈/비") : {}
            case ("눈") :
            {
                 /* Hue Color Value */ mWeatherState[0] = 4;
                /* Weather ICon Value */ mWeatherState[1] = R.drawable.ic_snow; break;
            }
        }

        /* Weather ICon Change */
        if(mImageView != null) { Glide.with(mContext).load(mWeatherState[1]).error(R.drawable.ic_thunder).into(mImageView); }

        return mWeatherColor[mWeatherState[0]];
    }

    /* ChangDFSXY */
    private void changeDFSXY(double mMapX, double mMapY)
    {
                        /* LCC Value */
        /* Double */
        final double RE = 6371.00877; /* 지구의 반경 (Km) */
        final double GRID = 5.0; /* 격자 간격 (Km) */
        final double SLAT1 = 30.0; /* 투영 위도 1 */
        final double SLAT2 = 60.0; /* 투영 위도 1 */
        final double OLON = 126.0; /* 기준점 경도 */
        final double OLAT = 38.0; /* 기준점 위도 */

        /* Int */
        final int XO = 43; /* 기준점 X 좌표 */
        final int YO = 136; /* 기준점 Y 좌표 */

                        /* DFS Value */
        /* Double */
        final double DEGRAD = Math.PI / 180.0;
        final double RADDEG = 180.0 / Math.PI;
        final double DRE = RE / GRID;
        final double DSLAT1 = SLAT1 * DEGRAD;
        final double DSLAT2 = SLAT2 * DEGRAD;
        final double DOLON = OLON * DEGRAD;
        final double DOLAT = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + DSLAT2 * 0.5) / Math.tan(Math.PI * 0.25 + DSLAT1 * 0.5);
        sn = Math.log(Math.cos(DSLAT1) / Math.cos(DSLAT2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + DSLAT1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(DSLAT1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + DOLAT * 0.5);
        ro = DRE * sf / Math.pow(ro, sn);

        /* 좌표 변환 */
        double ra = Math.tan(Math.PI * 0.25 + (mMapX) * DEGRAD * 0.5);
        ra = DRE * sf / Math.pow(ra, sn);
        double theta = mMapY * DEGRAD - DOLON;
        if(theta > Math.PI) { theta -= 2.0 * Math.PI; }
        if(theta < -Math.PI) { theta += 2.0 * Math.PI; }
        theta *= sn;

        mGridX = (int)Math.floor(ra * Math.sin(theta) + XO + 0.5);
        mGridY = (int)Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        mURL = String.format("http://www.kma.go.kr/wid/queryDFS.jsp?gridx=%d&gridy=%d", mGridX, mGridY);
    }
}

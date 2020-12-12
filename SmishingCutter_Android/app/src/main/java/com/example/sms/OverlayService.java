package com.example.sms;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class OverlayService extends Service {
    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;

    private float START_X, START_Y;
    private int PREV_X, PREV_Y;
    private int MAX_X = -1, MAX_Y = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // 최상위 윈도우에 넣기 위한 설정
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // FLAG_NOT_FOCUSABLE: 투명, FLAG_DIM_BEHIND : 불투명
                    PixelFormat.TRANSLUCENT);
        } else {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        Point point = new Point();
        mWindowManager.getDefaultDisplay().getSize(point);
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.width = (int)(point.x*0.8);
        mParams.height = (int)(point.y*0.3);
        mParams.x = (point.x-mParams.width)/2;
        mParams.y = 2*(point.y-mParams.height)/3;


        mView = inflater.inflate(R.layout.overlay, null);
        final Button closeButton = (Button) mView.findViewById(R.id.button_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });
        mWindowManager.addView(mView, mParams);
        mView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(MAX_X == -1)
                            setMaxPosition();
                        START_X = event.getRawX();
                        START_Y = event.getRawY();
                        PREV_X = mParams.x;
                        PREV_Y = mParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = (int)(event.getRawX() - START_X);
                        int y = (int)(event.getRawY() - START_Y);

                        //터치 이동
                        mParams.x = PREV_X + x;
                        mParams.y = PREV_Y + y;
                        optimizePosition();
                        mWindowManager.updateViewLayout(mView, mParams);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        final TextView textView = (TextView) mView.findViewById(R.id.textView);

        int url_no = intent.getIntExtra("url_num", 0);
        String resultMsg = "";

        for(int i=0; i<url_no; i++){
            String url = intent.getStringExtra("url_"+i);
            String url_block = intent.getStringExtra("url_block_"+i);
            Log.d(this.getClass().getName(), url+" = "+url_block);
            if(i!=0)
                resultMsg = resultMsg+"<br><br>";
            if(url_block.equals("0")){
                resultMsg = resultMsg+url+"는 <font color=\"#EE0022\"><b><u>위험한 URL</u></b></font>입니다!";
            } else if(url_block.equals("-1")){
                resultMsg = resultMsg+url+"는 <font color=\"#FFC81D\"><b><u>존재하지 않거나, 가져올 수 없는 URL</u></b></font>입니다. 조심해주세요.";
            } else{
                resultMsg = resultMsg+url+"는 <font color=\"#40C355\"><b><u>안전한 URL</u></b></font>입니다!";
            }
        }

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(resultMsg, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
        } else {
            textView.setText(Html.fromHtml(resultMsg), TextView.BufferType.SPANNABLE);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        if (mWindowManager != null) {
            if (mView != null)
                mWindowManager.removeView(mView);
        }
        super.onDestroy();
    }

    // 뷰의 위치가 화면 안에 있도록 설정
    private void setMaxPosition() {
        DisplayMetrics matrix = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(matrix);
        MAX_X = matrix.widthPixels - mView.getWidth();
        MAX_Y = matrix.heightPixels - mView.getHeight();
    }


    // 위치의 최대-최소값 검사
    private void optimizePosition() {
        if(mParams.x > MAX_X) mParams.x = MAX_X;
        if(mParams.y > MAX_Y) mParams.y = MAX_Y;
        if(mParams.x < 0) mParams.x = 0;
        if(mParams.y < 0) mParams.y = 0;
    }

    // 가로/세로 모드 변경 시 최대값 재설정
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setMaxPosition();
        optimizePosition();
    }

    // 단말기 번호 가져오기
    public String getPhoneNo(Context context){
        SQLiteDatabase db = context.openOrCreateDatabase("PHONE_NO", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT phone_no from PHONE_NO;", null);
        String phone_num = "none";
        if(cursor.moveToNext()){
            phone_num = cursor.getString(0);
        }
        db.close();
        return phone_num;
    }
}
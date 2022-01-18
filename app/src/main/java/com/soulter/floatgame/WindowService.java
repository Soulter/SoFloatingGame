

package com.soulter.floatgame;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.io.File;

import static com.soulter.floatgame.MainActivity.PIC_GESHI;
import static com.soulter.floatgame.MainActivity.fileIsExists;


/**
 * @author Soulter
 * @author's qq: 905617992
 *
 */
public class WindowService extends Service implements OnTouchListener {

    private static final String FNAME = "FloatGame";
    private static final String CVTag = "cvtag";
    private static final String posixTag = "posixtag";
    private static final String posiyTag = "posiytag";
    private static final String WINALPHA_1 = "winalpha1";
    private static final String WINALPHA_2 = "winalpha2";
    private static final int MAIN_WIN_SIZE = 150;
    public static final int DEFAULT_ALPHA_1 = 150;
    public static final int DEFAULT_ALPHA_2 = 50;
    private String TAG = this.getClass().getSimpleName();
    private WindowManager mWindowManager;
    int statusBarHeight = -1;
    private WindowManager.LayoutParams mParams;
    private LinearLayout mainWinLayout;

    private ImageView mainWinView;
    private GameWindowView gameWindowView;

    private int moveTAG = 0;
    private int spfsTemp = 0; //预先存储用户选择的游戏，防止二次调用浪费资源.
    private int preX,preY,x,y,aftX,aftY,ppreX,ppreY; //pre and aft : 比对是否为Click
    private int deviceX,deviceY;

//
//    private int paXbak=0;
//    private int paYbak=0;

//    透明度
    private int alpha1 = DEFAULT_ALPHA_1;
    private int alpha2 = DEFAULT_ALPHA_2;

    private int showingWhat = 0;



    @SuppressLint("HandlerLeak")
    private Handler winHandler = new Handler(){
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            sendEmptyMessageDelayed(0,2500);

            mainWinView.setAlpha(alpha2);//设置主window的透明度

        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LayoutInflater inflater = LayoutInflater.from(this);
        mainWinLayout = (LinearLayout) inflater.inflate(R.layout.main_win_view, null);
        mainWinView = (ImageView)mainWinLayout.findViewById(R.id.main_win_image);

        //设置主window的透明度
        alpha1 = (int)ShaPreUtils.getFromSpfs(this, WINALPHA_1, DEFAULT_ALPHA_1);
        alpha2 = (int)ShaPreUtils.getFromSpfs(this, WINALPHA_2, DEFAULT_ALPHA_2);
        mainWinView.setAlpha(alpha1);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // set layout parameter of window manager
        mParams = new WindowManager.LayoutParams();
        mParams.width = MAIN_WIN_SIZE;
        mParams.height = MAIN_WIN_SIZE;
        //设置悬浮窗：不获取焦点|可扩展到屏幕外。
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        if(Build.VERSION.SDK_INT >= 26)
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.gravity = Gravity.START |Gravity.TOP;

        mParams.format = PixelFormat.RGBA_8888; //设置效果透明

        mainWinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("TAG","Click.");
                openApp();
            }
        });
//        mainWinLayout.setOnTouchListener(this);

        mParams.x = (int)ShaPreUtils.getFromSpfs(WindowService.this,posixTag,0);
        mParams.y = (int)ShaPreUtils.getFromSpfs(WindowService.this,posiyTag,deviceY/2);

        mWindowManager.addView(mainWinLayout, mParams);

        winHandler.sendEmptyMessageDelayed(0,2500);


        deviceX = mWindowManager.getDefaultDisplay().getWidth();
        deviceY = mWindowManager.getDefaultDisplay().getHeight();
        spfsTemp = (int) ShaPreUtils.getFromSpfs(this, CVTag, 1);

        //自定义悬浮窗
        if (fileIsExists(Environment.getExternalStorageDirectory()+ File.separator+FNAME+File.separator+"win"+PIC_GESHI)) {
            try {
                Bitmap winPhoto = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + File.separator + FNAME + File.separator + "win"+PIC_GESHI);
                mainWinView.setImageBitmap(winPhoto);
            } catch (Exception ignore) {
            }
        }



    }

    //横竖屏切换
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mParams.x = 0;
        mParams.y = deviceX/2;
        mWindowManager.updateViewLayout(mainWinLayout, mParams);
    }

    @Override
    public void onDestroy() {

        if(mWindowManager != null) {
            if(mainWinLayout != null && showingWhat == 0) {
                mWindowManager.removeView(mainWinLayout);

            }
            if (gameWindowView != null && showingWhat == 1) {
                mWindowManager.removeView(gameWindowView);

            }
            mainWinLayout = null;
            gameWindowView = null;
        }
        ShaPreUtils.putToSpfs(WindowService.this,posixTag,mParams.x);
        ShaPreUtils.putToSpfs(WindowService.this,posiyTag,mParams.y);
        winHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://神TM这是按下激活,BUT THAT'S GOOD FOR ME!HAHAHA
                //TODO:turn bright;
                winHandler.removeCallbacksAndMessages(null);

                mainWinView.setAlpha(alpha1);

                ppreX = (int)event.getRawX();
                ppreY = (int)event.getRawY();
                preX = (int)event.getRawX();
                preY = (int)event.getRawY();
                Log.v("TAG","onTouch-> ACTION_DOWN");
                return false;
            case MotionEvent.ACTION_MOVE:
                Log.v("TAG","onTouch-> ACTION_MOVE");


                x = (int) event.getRawX();
                y = (int) event.getRawY();
                mParams.x += x - preX;
                mParams.y += y - preY;
                Log.v("TAG","X,Y:"+mParams.x+" "+mParams.y);
                mWindowManager.updateViewLayout(mainWinLayout, mParams);
                preX = x;
                preY = y;
                return false;
            default:
                break;
        }
        Log.v("TAG", "on Touch->None");

        //COMPLETE: 展开webview的move功能时，如果一直往左滑，退出成圆圈后会很难。
        if (mParams.x > deviceX-MAIN_WIN_SIZE)
            mParams.x = deviceX-MAIN_WIN_SIZE+100;
        if (mParams.x < 0)
            mParams.x = -90;
        if (mParams.y > deviceY-MAIN_WIN_SIZE)
            mParams.y = deviceY-MAIN_WIN_SIZE;
        if (mParams.y < 0)
            mParams.y = statusBarHeight;

        mWindowManager.updateViewLayout(mainWinLayout, mParams);

        winHandler.sendEmptyMessageDelayed(0,2500);

        aftX = (int)event.getRawX();
        aftY = (int)event.getRawY();

        if (moveTAG == 1) return true;



        Log.v("TAG","ppreX,Y:"+ppreX+" "+ppreY+"\naftX,Y:"+aftX+" "+aftY);
        if(Math.abs(aftX-ppreX)<=10 && Math.abs(aftY-ppreY)<=10){
            Log.v("TAG","Judging to a Click Event. X"+Math.abs(aftX-preX)+" Y"+Math.abs(aftY-preY));
            return false;
        }
        return true;                                    //神TM这是松开激活
    }


    public void openApp(){
        if (spfsTemp == 0 || spfsTemp == 1){
            gameWindowView = new GameWindowView(this);
            mWindowManager.removeView(mainWinLayout);
            mWindowManager.addView(gameWindowView, mParams);
            showingWhat = 1;
        }
    }


//    public void openGame(){
//
//        winHandler.removeCallbacksAndMessages(null);
//
//        myImageView.setVisibility(View.GONE);
//        myWebView.setVisibility(View.VISIBLE);
//        if (spfsTemp == 1){
//            paXbak = mParams.x;
//            paYbak = mParams.y;
//            mParams.x = 0;
//            mParams.width = deviceX;
//            mParams.height = 450;
//        }
//        if (spfsTemp == 2){
//            paXbak = mParams.x;
//            paYbak = mParams.y;
//            if (mParams.x>deviceX-600)
//                mParams.x=deviceX-600;
//            if (mParams.x<0)
//                mParams.x=0;
//            mParams.width = 600;
//            mParams.height = 900;
//        }
//        if (webStartTAG == 1) {
//            myWebView.getSettings().setJavaScriptEnabled(true);
//            myWebView.setWebChromeClient(new WebChromeClient());
//            myWebView.setWebViewClient(new WebViewClient() {
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    view.loadUrl(url);
//                    return true;
//                }
//            });
//            myWebView.requestFocus();
//            myWebView.getSettings().setDomStorageEnabled(true);
//
//            Log.v("TAG", "get spfs:" + String.valueOf(ShaPreUtils.getFromSpfs(this, CVTag, 1)));
//            //获取要玩哪一个游戏
//            if (spfsTemp == 1)
//                myWebView.loadUrl("file:///android_asset/dino/index.html");
//            if (spfsTemp == 2)
//                myWebView.loadUrl("file:///android_asset/2048/index.html");
//        }
//
//
//        webBtnExit = (ImageView) winLayout.findViewById(R.id.webBtnExit);
//        webBtnExit.setVisibility(View.VISIBLE);
//        webBtnExit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                winHandler.sendEmptyMessageDelayed(0,2500);
//                mParams.x = paXbak;
//                mParams.y = paYbak;
//                myImageView.setAlpha(alpha1);
//                myImageView.setVisibility(View.VISIBLE);
//                myWebView.setVisibility(View.INVISIBLE);
//                webStartTAG = 0;
//                webBtnExit.setVisibility(View.GONE);
//                webBtnMove.setVisibility(View.GONE);
//                webMoveLyo.setVisibility(View.GONE);
//                moveTAG = 0;
//                webBtnMove.setImageResource(R.drawable.movewin);
//                mParams.height = 150;
//                mParams.width = 150;
//                mWindowManager.updateViewLayout(winLayout,mParams);
//
//
////                //TODO:重启WM，销毁WebView以节省资源
////                if(mWindowManager != null) {
////                    if(winLayout != null) mWindowManager.removeView(winLayout);
////                }
////                mWindowManager.addView(winLayout, mParams);
//
//            }
//        });
//
//
//        webBtnMove.setVisibility(View.VISIBLE);
//        webBtnMove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (moveTAG==0) {
//                    webMoveLyo.setVisibility(View.VISIBLE);
//                    webBtnMove.setImageResource(R.drawable.movingwin);
//                    myWebView.setVisibility(View.INVISIBLE);
//                    moveTAG = 1;
//
//                }else {
//                    webMoveLyo.setVisibility(View.GONE);
//                    webBtnMove.setImageResource(R.drawable.movewin);
//                    myWebView.setVisibility(View.VISIBLE);
//                    moveTAG = 0;
//                }
//            }
//        });
//
//
//        //按下主悬浮窗，显示Web。
//        mWindowManager.updateViewLayout(winLayout,mParams);
//        Log.v("TAG","WebView set ok");
//    }

}

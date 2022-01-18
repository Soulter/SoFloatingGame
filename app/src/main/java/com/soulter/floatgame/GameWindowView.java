package com.soulter.floatgame;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @author Soulter
 * @author's qq: 905617992
 *
 */
public class GameWindowView extends FrameLayout {

    private Context mContext;
    private WindowManager mWindowManager;

    private static final String CVTag = "cvtag";

    private WebView gameWebView;

    private int preX,preY,x,y; //pre and aft : 比对是否为Click

    private int spfsTemp = 0; //预先存储用户选择的游戏，防止二次调用浪费资源.


    WindowManager.LayoutParams mParams;




    public GameWindowView(Context context){
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        spfsTemp = (int) ShaPreUtils.getFromSpfs(getContext(), CVTag, 1);

        mWindowManager= (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //a view inflate itself, that's funny
        inflate(mContext,R.layout.game_win_web_view,this);
        gameWebView = (WebView)findViewById(R.id.gameWebView);

        gameWebView.getSettings().setJavaScriptEnabled(true);
        gameWebView.setWebChromeClient(new WebChromeClient());
        gameWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        gameWebView.requestFocus();
        gameWebView.getSettings().setDomStorageEnabled(true);

        Log.v("TAG", "get spfs:" + String.valueOf(ShaPreUtils.getFromSpfs(getContext(), CVTag, 1)));
        //获取要玩哪一个游戏
        if (spfsTemp == 1)
            gameWebView.loadUrl("file:///android_asset/dino/index.html");
        if (spfsTemp == 2)
            gameWebView.loadUrl("file:///android_asset/2048/index.html");


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://神TM这是按下激活,BUT THAT'S GOOD FOR ME!HAHAHA

                preX = (int)event.getRawX();
                preY = (int)event.getRawY();
                Log.v("TAG","onTouch-> ACTION_DOWN");
                return false;
            case MotionEvent.ACTION_MOVE:
                Log.v("TAG","onTouch-> ACTION_MOVE");

                mParams= (WindowManager.LayoutParams) getLayoutParams();


                x = (int) event.getRawX();
                y = (int) event.getRawY();
                mParams.x += x - preX;
                mParams.y += y - preY;
                Log.v("TAG","X,Y:"+mParams.x+" "+mParams.y);
                mWindowManager.updateViewLayout(this, mParams);
                preX = x;
                preY = y;
                return false;
            default:
                break;
        }
        Log.v("TAG", "on Touch->None");

//        //COMPLETE: 展开webview的move功能时，如果一直往左滑，退出成圆圈后会很难。
//        if (mParams.x > deviceX-MAIN_WIN_SIZE)
//            mParams.x = deviceX-MAIN_WIN_SIZE+100;
//        if (mParams.x < 0)
//            mParams.x = -90;
//        if (mParams.y > deviceY-MAIN_WIN_SIZE)
//            mParams.y = deviceY-MAIN_WIN_SIZE;
//        if (mParams.y < 0)
//            mParams.y = statusBarHeight;

        mWindowManager.updateViewLayout(this, mParams);

        return true;                                    //神TM这是松开激活




//        return super.onTouchEvent(event);
    }


}

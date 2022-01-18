package com.soulter.floatgame;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;


/**
 * @author Soulter
 * @author's qq: 905617992
 *
 */

public class SetinListView extends ListView {

    public SetinListView(Context context) {
        super(context);
    }

    public SetinListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SetinListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量的大小由一个32位的数字表示，前两位表示测量模式，后30位表示大小，这里需要右移两位才能拿到测量的大小
        int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}

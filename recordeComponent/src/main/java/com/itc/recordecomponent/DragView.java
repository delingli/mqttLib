package com.itc.recordecomponent;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class DragView extends RelativeLayout {

    private View view;

    public DragView(Context context) {
        this(context, null);
    }

    public DragView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        view = LayoutInflater.from(context).inflate(R.layout.lubo_layout, this, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//获取当前点的xy位置
        int currentX = (int) event.getX();
        int currentY = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (windowManager == null) {
                    setWindowParams(currentX, currentY);
                    windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                    ViewGroup viewGroup = (ViewGroup) view.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeAllViews();
                    }
                    windowManager.addView(view, windowParams);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                windowParams.x = currentX;
                windowParams.y = currentY;
                windowManager.updateViewLayout(view, windowParams);
                break;
            case MotionEvent.ACTION_UP:
                 windowManager.removeView(view);
                break;
        }
        return true;
    }

    private WindowManager windowManager;// 用于可拖动的浮动窗口
    private WindowManager.LayoutParams windowParams;// 浮动窗口的参数

    private void setWindowParams(int x, int y) {
        // 建立item的缩略图
        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;// 这个必须加
        // 得到preview左上角相对于屏幕的坐标
        windowParams.x = x;
        windowParams.y = y;
        // 设置宽和高
        windowParams.width = 200;
        windowParams.height = 200;
        windowParams.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
//        windowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            windowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            windowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }*/
    }

}

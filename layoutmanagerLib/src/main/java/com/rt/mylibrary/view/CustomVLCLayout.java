package com.rt.mylibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.videolan.libvlc.util.VLCVideoLayout;

public class CustomVLCLayout extends VLCVideoLayout {
    private int width = -1;
    private int height = -1;

    public CustomVLCLayout(@NonNull Context context, int width, int height) {
        super(context);
        this.width = width;
        this.height = height;
    }

    public CustomVLCLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVLCLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomVLCLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewGroup.LayoutParams lp = this.getLayoutParams();
        lp.height = width;
        lp.width = height;
        this.setLayoutParams(lp);
    }
}

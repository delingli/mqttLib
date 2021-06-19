package com.johnson.arcface2camerax.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.johnson.arcface2camerax.model.DrawInfo;
import com.johnson.arcface2camerax.utils.DrawHelper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FaceRectView extends View {
    private static final String TAG = "FaceRectView";
    private CopyOnWriteArrayList<DrawInfo> faceRectList = new CopyOnWriteArrayList<>();

    public FaceRectView(Context context) {
        this(context, null);
    }

    public FaceRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (faceRectList != null && faceRectList.size() > 0) {
            for (DrawInfo drawInfo : faceRectList) {
                DrawHelper.drawFaceRect(canvas, drawInfo, Color.GREEN, 5);
            }
        }
    }

    public void clearFaceInfo() {
        faceRectList.clear();
        postInvalidate();
    }

    public void addFaceInfo(DrawInfo faceInfo) {
        faceRectList.add(faceInfo);
        postInvalidate();
    }

    public void addFaceInfo(List<DrawInfo> faceInfoList) {
        faceRectList.addAll(faceInfoList);
        postInvalidate();
    }
}
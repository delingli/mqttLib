package com.itc.recordecomponent;

import android.app.Activity;
import android.view.View;

import androidx.fragment.app.Fragment;

import java.util.TimerTask;

public class Text {
    TimerTask timerTask;

    public void startClick(Fragment activity) {
        timerTask = new TimerTask() {
            int cnt = 0;

            @Override
            public void run() {

            }
        };
//        timer1.schedule(timerTask, 0, 1000);
    }
}

package com.itc.setconfig;

import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

public
class ConfigUtils {
    public static AlertDialog getDialog (Context context, int resId){
        //  AlertDialog dialog = new getShow.Builder(context, AlertDialog.THEME_TRADITIONAL).create();     //AlertDialog.THEME_TRADITIONAL表示默认的背景为透明
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.tipsDialog_style).create();     //AlertDialog.THEME_TRADITIONAL表示默认的背景为透明
        dialog.setView(new EditText(context));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setContentView(resId);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
//        int screenWidth = ScreenUtil.getInstance(UiUtils.getContext()).getScreenWidth();
//        attributes.width = screenWidth;
        attributes.gravity = Gravity.CENTER;
        window.setAttributes(attributes);
        return dialog;
    }
}

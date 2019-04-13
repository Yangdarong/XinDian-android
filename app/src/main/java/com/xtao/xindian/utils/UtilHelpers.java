package com.xtao.xindian.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class UtilHelpers {

    /**
     * 比对用户焦点坐标和控件坐标，判断是否隐藏
     * @param event     焦点位置
     * @param view      控件 view
     * @param activity
     */
    public static void hidKeyboard(MotionEvent event, View view, Activity activity) {
        try {
            if (view instanceof EditText) {
                int[] location = {0, 0};
                view.getLocationInWindow(location);
                int left = location[0], top = location[1];
                int right = left + view.getWidth();
                int bottom = top + view.getHeight();

                // 判断焦点位置是否在空间内
                if (event.getRawX() < left || event.getRawX() > right ||
                        event.getY() < top || event.getRawY() > bottom) {

                    // 隐藏键盘
                    IBinder token = view.getWindowToken();
                    InputMethodManager inputMethodManager = (InputMethodManager) activity
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(token,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

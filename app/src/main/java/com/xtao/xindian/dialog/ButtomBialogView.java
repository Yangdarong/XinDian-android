package com.xtao.xindian.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ButtomBialogView extends Dialog {

    private boolean isCancelable;       // 控制点击dialog外部是否dismiss
    private boolean isBackCancelable;   // 控制返回键是否dismiss
    private View view;
    private Context context;

    public ButtomBialogView(@NonNull Context context, View view, boolean isCancelable, boolean isBackCancelable) {
        super(context);

        this.context = context;
        this.view = view;
        this.isCancelable = isCancelable;
        this.isBackCancelable = isBackCancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(view);
        setCancelable(isCancelable);
        setCanceledOnTouchOutside(isBackCancelable);

        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

    }
}

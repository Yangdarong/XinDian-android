package com.xtao.xindian.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xtao.xindian.R;
import com.xtao.xindian.view.NumberControllerView;

public class SettleFoodDialog extends Dialog implements View.OnClickListener {

    private NumberControllerView ncvDialogSettleFoodNum;
    private TextView tvDialogSettleFoodCancel;
    private TextView tvDialogSettleFoodSubmit;
    private OnCloseListener listener;

    private int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        ncvDialogSettleFoodNum = findViewById(R.id.ncv_dialog_settle_food_num);
        ncvDialogSettleFoodNum.setValueChangeListener(new NumberControllerView.onNumChangedListener() {
            @Override
            public void addValueListener(View v, int value) {
                setValue(value);
            }

            @Override
            public void subValueListener(View v, int value) {
                setValue(value);
            }
        });
        tvDialogSettleFoodCancel = findViewById(R.id.tv_dialog_settle_food_cancel);
        tvDialogSettleFoodCancel.setOnClickListener(this);
        tvDialogSettleFoodSubmit = findViewById(R.id.tv_dialog_settle_food_submit);
        tvDialogSettleFoodSubmit.setOnClickListener(this);
    }

    public SettleFoodDialog(@NonNull Context context, View layout, OnCloseListener listener) {
        super(context);
        setContentView(layout);
        Window window = getWindow();
        assert window != null;
        WindowManager.LayoutParams params = window.getAttributes();

        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        this.listener = listener;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_dialog_settle_food_cancel:
                if (listener != null) {
                    listener.onClick(this, false, getValue());
                }
                this.dismiss();
                break;
            case R.id.tv_dialog_settle_food_submit:
                if (listener != null) {
                    listener.onClick(this, true, getValue());
                }
                this.dismiss();
                break;
        }
    }


    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm, int value);
    }
}

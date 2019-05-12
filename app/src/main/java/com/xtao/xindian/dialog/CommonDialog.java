package com.xtao.xindian.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xtao.xindian.R;

public class CommonDialog extends Dialog implements View.OnClickListener {
    private TextView tvContent;
    private TextView tvTitle;
    private TextView tvSubmit;
    private TextView tvCancel;

    private Context mContext;
    private String content;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;

    public CommonDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public CommonDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public CommonDialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
    }

    public CommonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public CommonDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public CommonDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public CommonDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_common);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        tvContent = findViewById(R.id.content);
        tvTitle = (TextView)findViewById(R.id.title);
        tvSubmit = (TextView)findViewById(R.id.submit);
        tvSubmit.setOnClickListener(this);
        tvCancel = (TextView)findViewById(R.id.cancel);
        tvCancel.setOnClickListener(this);

        tvContent.setText(content);
        if(!TextUtils.isEmpty(positiveName)){
            tvSubmit.setText(positiveName);
        }

        if(!TextUtils.isEmpty(negativeName)){
            tvCancel.setText(negativeName);
        }

        if(!TextUtils.isEmpty(title)){
            tvTitle.setText(title);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                if(listener != null){
                    listener.onClick(this, false);
                }
                this.dismiss();
                break;
            case R.id.submit:
                if(listener != null){
                    listener.onClick(this, true);
                }
                break;
        }
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm);
    }
}

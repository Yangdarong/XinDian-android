package com.xtao.xindian.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xtao.xindian.R;

public class NumberControllerView extends LinearLayout implements View.OnClickListener {

    private View view ;
    private Context context ;
    private LayoutInflater inflater ;

    private Button btnNumControllerAdd;
    private Button btnNumControllerSub;
    private TextView num;

    private int value ;
    private int minValue ;
    private int maxValue ;
    private Drawable addButtonBackGround ;
    private Drawable subButtonBackGround ;
    private Drawable numTextBackGround ;
    private TypedArray typedArray ;

    private onNumChangedListener listener;

    public NumberControllerView(Context context) {
        this(context, null);
    }

    public NumberControllerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
        inflater = LayoutInflater.from(context);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumControllerView);

        initView();
        typedArray.recycle();
    }

    private void initView() {
        view = inflater.inflate(R.layout.layout_numcontroller_mer , null , false) ;

        btnNumControllerAdd = (Button) view.findViewById(R.id.btn_number_add) ;
        btnNumControllerSub = (Button) view.findViewById(R.id.btn_number_sub) ;
        num = (TextView) view.findViewById(R.id.tv_num) ;

        getAttrValue() ;

        btnNumControllerAdd.setBackground(addButtonBackGround);
        btnNumControllerSub.setBackground(subButtonBackGround);
        num.setBackground(numTextBackGround);

        num.setText(value+"");

        btnNumControllerAdd.setOnClickListener(this);
        btnNumControllerSub.setOnClickListener(this);
        num.setOnClickListener(this);

        addView(view);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
    }

    private void getAttrValue() {
        maxValue = typedArray.getInt(R.styleable.NumControllerView_maxValue , 0) ;
        value = typedArray.getInt(R.styleable.NumControllerView_value , 0) ;
        minValue = typedArray.getInt(R.styleable.NumControllerView_minValue , 0) ;

        addButtonBackGround = typedArray.getDrawable(R.styleable.NumControllerView_addButtonBackground  ) ;
        subButtonBackGround = typedArray.getDrawable(R.styleable.NumControllerView_subButtonBackground ) ;
        numTextBackGround = typedArray.getDrawable(R.styleable.NumControllerView_numTextBackGround ) ;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_number_add) {
            if (addValue() && listener != null)
                listener.addValueListener(v , getValue());
        } else if (v.getId() == R.id.btn_number_sub) {
            if (subValue() && listener != null)
                listener.subValueListener(v , getValue());
        }
    }

    private Boolean addValue(){
        if( num.getText()!=null && value != maxValue ) {
            value++ ;
            num.setText(value+"");
            return true ;
        }else {
            Toast.makeText( context ,"已达到允许的最大值" , Toast.LENGTH_SHORT ).show();
            return false ;
        }
    }

    private Boolean subValue(){
        if( num.getText()!=null && value != minValue ) {
            value-- ;
            num.setText(value+"");
            return true ;
        }else {
            Toast.makeText( context ,"已达到允许的最小值" , Toast.LENGTH_SHORT ).show();
            return false ;
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        num.setText(value + "");
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public interface onNumChangedListener {
        void addValueListener(View v, int value) ;
        void subValueListener(View v, int value) ;
    }

    public void setValueChangeListener(onNumChangedListener listener) {
        this.listener = listener;
    }
}

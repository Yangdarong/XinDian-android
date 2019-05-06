package com.xtao.xindian.common;


public class CommonResultType {
    private int state;          // 状态码

    private String message;     // 信息

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

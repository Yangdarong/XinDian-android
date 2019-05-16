package com.xtao.xindian.pojo;

import java.sql.Timestamp;

public class TbStrategy {

    private int sId;
    private String sName;
    private String sContext;
    private int sState;
    private Timestamp sCreateTime;
    private TbUser user;

    public TbStrategy() {
    }

    public int getsId() {
        return sId;
    }

    public void setsId(int sId) {
        this.sId = sId;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsContext() {
        return sContext;
    }

    public void setsContext(String sContext) {
        this.sContext = sContext;
    }

    public int getsState() {
        return sState;
    }

    public void setsState(int sState) {
        this.sState = sState;
    }

    public Timestamp getsCreateTime() {
        return sCreateTime;
    }

    public void setsCreateTime(Timestamp sCreateTime) {
        this.sCreateTime = sCreateTime;
    }

    public TbUser getUser() {
        return user;
    }

    public void setUser(TbUser user) {
        this.user = user;
    }
}

package com.xtao.xindian.pojo;

import java.sql.Timestamp;

public class TbOrderUser {
    private int ouId;
    private int uId;
    private float ouCost;
    private TbUser user;
    private Timestamp ouPayTime;

    public TbOrderUser() {
    }

    public int getOuId() {
        return ouId;
    }

    public void setOuId(int ouId) {
        this.ouId = ouId;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public float getOuCost() {
        return ouCost;
    }

    public void setOuCost(float ouCost) {
        this.ouCost = ouCost;
    }

    public TbUser getUser() {
        return user;
    }

    public void setUser(TbUser user) {
        this.user = user;
    }

    public Timestamp getOuPayTime() {
        return ouPayTime;
    }

    public void setOuPayTime(Timestamp ouPayTime) {
        this.ouPayTime = ouPayTime;
    }
}

package com.xtao.xindian.pojo;

import java.sql.Timestamp;

public class TbOrder {

    private int oId;
    private int oState;
    private int mId;
    private int uId;
    private String uPhone;
    private int oPayState;
    private Timestamp oCreateTime;
    private Timestamp oFinishTime;
    private String oAddress;
    private String oNote;
    private float oCost;
    private int ouId;
    private TbOrderUser orderUser;

    // 外键
    private TbMer   mer;
    private TbUser  user;

    public TbOrder() {
    }

    public int getoId() {
        return oId;
    }

    public void setoId(int oId) {
        this.oId = oId;
    }

    public int getoState() {
        return oState;
    }

    public void setoState(int oState) {
        this.oState = oState;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public String getuPhone() {
        return uPhone;
    }

    public void setuPhone(String uPhone) {
        this.uPhone = uPhone;
    }

    public int getoPayState() {
        return oPayState;
    }

    public void setoPayState(int oPayState) {
        this.oPayState = oPayState;
    }

    public Timestamp getoCreateTime() {
        return oCreateTime;
    }

    public void setoCreateTime(Timestamp oCreateTime) {
        this.oCreateTime = oCreateTime;
    }

    public Timestamp getoFinishTime() {
        return oFinishTime;
    }

    public void setoFinishTime(Timestamp oFinishTime) {
        this.oFinishTime = oFinishTime;
    }

    public String getoAddress() {
        return oAddress;
    }

    public void setoAddress(String oAddress) {
        this.oAddress = oAddress;
    }

    public String getoNote() {
        return oNote;
    }

    public void setoNote(String oNote) {
        this.oNote = oNote;
    }

    public TbMer getMer() {
        return mer;
    }

    public void setMer(TbMer mer) {
        this.mer = mer;
    }

    public TbUser getUser() {
        return user;
    }

    public void setUser(TbUser user) {
        this.user = user;
    }

    public float getoCost() {
        return oCost;
    }

    public void setoCost(float oCost) {
        this.oCost = oCost;
    }

    public int getOuId() {
        return ouId;
    }

    public void setOuId(int ouId) {
        this.ouId = ouId;
    }

    public TbOrderUser getOrderUser() {
        return orderUser;
    }

    public void setOrderUser(TbOrderUser orderUser) {
        this.orderUser = orderUser;
    }
}

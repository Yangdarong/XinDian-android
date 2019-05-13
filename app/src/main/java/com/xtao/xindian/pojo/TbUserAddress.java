package com.xtao.xindian.pojo;

public class TbUserAddress {
    private int uaId;
    private int uId;
    private String uaAddress;
    private int uaIsUsual;

    private TbUser user;

    public TbUserAddress() {
    }

    public int getUaId() {
        return uaId;
    }

    public void setUaId(int uaId) {
        this.uaId = uaId;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public String getUaAddress() {
        return uaAddress;
    }

    public void setUaAddress(String uaAddress) {
        this.uaAddress = uaAddress;
    }

    public int getUaIsUsual() {
        return uaIsUsual;
    }

    public void setUaIsUsual(int uaIsUsual) {
        this.uaIsUsual = uaIsUsual;
    }

    public TbUser getUser() {
        return user;
    }

    public void setUser(TbUser user) {
        this.user = user;
    }
}

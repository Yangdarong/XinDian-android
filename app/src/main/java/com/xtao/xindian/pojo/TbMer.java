package com.xtao.xindian.pojo;

import java.util.List;

// 后台管理员用户
public class TbMer {

    private int mId;
    private String mLoginId;
    private String mPassword;
    private String mName;
    private String mAddress;
    private String mPhone;
    private String mIntro;
    private String mUrl;
    private int mIsAdmin;

    public TbMer() {
    }

    public TbMer(int mId, String mLoginId, String mPassword, String mName, String mAddress, String mPhone, String mIntro, int mIsAdmin, String mUrl) {
        this.mId = mId;
        this.mLoginId = mLoginId;
        this.mPassword = mPassword;
        this.mName = mName;
        this.mAddress = mAddress;
        this.mPhone = mPhone;
        this.mIntro = mIntro;
        this.mIsAdmin = mIsAdmin;
        this.mUrl = mUrl;
    }

    public String getmIntro() {
        return mIntro;
    }

    public void setmIntro(String mIntro) {
        this.mIntro = mIntro;
    }


    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmLoginId() {
        return mLoginId;
    }

    public void setmLoginId(String mLoginId) {
        this.mLoginId = mLoginId;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public int getmIsAdmin() {
        return mIsAdmin;
    }

    public void setmIsAdmin(int mIsAdmin) {
        this.mIsAdmin = mIsAdmin;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

}

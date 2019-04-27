package com.xtao.xindian.pojo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 *  用户表
 */
public class TbUser implements Serializable {

    private int uId;

    private String uLoginId;

    private String uPassword;

    private String uSignature;

    private String uSex;

    private String uMail;

    private String uPhone;

    private String uHeadPortrait;

    private Timestamp uLoginTime;

    private int uUserStateId;

    public TbUser(String uLoginId, String uPassword) {
        this.uLoginId = uLoginId;
        this.uPassword = uPassword;
    }

    public TbUser() {
    }

    public TbUser(int uId, String uSignature, String uSex, String uMail, String uPhone, String uHeadPortrait, int uUserStateId) {
        this.uId = uId;
        this.uSignature = uSignature;
        this.uSex = uSex;
        this.uMail = uMail;
        this.uPhone = uPhone;
        this.uHeadPortrait = uHeadPortrait;
        this.uUserStateId = uUserStateId;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public String getuLoginId() {
        return uLoginId;
    }

    public void setuLoginId(String uLoginId) {
        this.uLoginId = uLoginId;
    }

    public String getuPassword() {
        return uPassword;
    }

    public void setuPassword(String uPassword) {
        this.uPassword = uPassword;
    }

    public String getuSignature() {
        return uSignature;
    }

    public void setuSignature(String uSignature) {
        this.uSignature = uSignature;
    }

    public String getuSex() {
        return uSex;
    }

    public void setuSex(String uSex) {
        this.uSex = uSex;
    }

    public String getuMail() {
        return uMail;
    }

    public void setuMail(String uMail) {
        this.uMail = uMail;
    }

    public String getuPhone() {
        return uPhone;
    }

    public void setuPhone(String uPhone) {
        this.uPhone = uPhone;
    }

    public String getuHeadPortrait() {
        return uHeadPortrait;
    }

    public void setuHeadPortrait(String uHeadPortrait) {
        this.uHeadPortrait = uHeadPortrait;
    }

    public Timestamp getuLoginTime() {
        return uLoginTime;
    }

    public void setuLoginTime(Timestamp uLoginTime) {
        this.uLoginTime = uLoginTime;
    }

    public int getuUserStateId() {
        return uUserStateId;
    }

    public void setuUserStateId(int uUserStateId) {
        this.uUserStateId = uUserStateId;
    }
}

package com.xtao.xindian.pojo;

import java.io.Serializable;

/**
 * 首页 导航栏标题
 */
public class TbTitle implements Serializable {

    private int tId;
    private String tName;
    private int tFrom;
    private String tUrl;

    public int gettId() {
        return tId;
    }

    public void settId(int tId) {
        this.tId = tId;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public int gettFrom() {
        return tFrom;
    }

    public void settFrom(int tFrom) {
        this.tFrom = tFrom;
    }

    public String gettUrl() {
        return tUrl;
    }

    public void settUrl(String tUrl) {
        this.tUrl = tUrl;
    }
}

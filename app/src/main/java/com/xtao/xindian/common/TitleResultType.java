package com.xtao.xindian.common;

import com.xtao.xindian.pojo.TbTitle;

import java.util.List;

/**
 *  查询TITLE 返回的Json格式
 */
public class TitleResultType {
    private int state;  // 1 返回成功 0 返回失败

    private List<TbTitle> titles;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<TbTitle> getTitles() {
        return titles;
    }

    public void setTitles(List<TbTitle> titles) {
        this.titles = titles;
    }
}

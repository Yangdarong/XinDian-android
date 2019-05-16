package com.xtao.xindian.common;



import com.xtao.xindian.pojo.TbStrategy;

import java.util.List;

/**
 * 封装攻略列表发送JSON MAPPER
 */
public class StrategiesResultType {
    private int state;
    private List<TbStrategy> strategies;
    private String message;

    public StrategiesResultType() {
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<TbStrategy> getStrategies() {
        return strategies;
    }

    public void setStrategies(List<TbStrategy> strategies) {
        this.strategies = strategies;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

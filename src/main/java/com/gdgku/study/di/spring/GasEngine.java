package com.gdgku.study.di.spring;

public class GasEngine implements Engine {

    @Override
    public String start() {
        return "휘발유 엔진 시동";
    }
}

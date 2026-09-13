package com.gdgku.study.di.container;

public class ElectricEngine implements Engine {

    @Override
    public String start() {
        return "전기 모터 구동";
    }
}

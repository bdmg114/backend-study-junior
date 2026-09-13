package com.gdgku.study.di.manual;

public class Car implements Vehicle {

    private final Engine engine;

    public Car(Engine engine) {
        this.engine = engine;
    }

    @Override
    public String drive() {
        return engine.start() + " -> 출발!";
    }
}

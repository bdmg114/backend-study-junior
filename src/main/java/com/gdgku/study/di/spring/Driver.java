package com.gdgku.study.di.spring;

public class Driver {

    private final Vehicle vehicle;

    public Driver(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String startTrip() {
        return "운전자: " + vehicle.drive();
    }
}

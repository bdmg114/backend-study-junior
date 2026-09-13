package com.gdgku.study.di.container;

/**
 * [2단계: 수제 DI 컨테이너 사용]
 *
 * ManualDiDemo와 달리, "무엇을 무엇으로 쓸지"만 SimpleContainer에 등록해두면 조립은
 * 컨테이너가 대신 해준다. 엔진을 바꾸고 싶으면 register() 한 줄만 바꾸면 되고, Car/Driver
 * 코드는 전혀 건드리지 않는다.
 */
public class ContainerDiDemo {

    public static String run() {
        SimpleContainer container = new SimpleContainer();
        container.register(Engine.class, GasEngine.class);
        container.register(Vehicle.class, Car.class);
        container.register(Driver.class, Driver.class);

        Driver driver = container.resolve(Driver.class);
        return driver.startTrip();
    }

    public static void main(String[] args) {
        System.out.println(run());
    }
}

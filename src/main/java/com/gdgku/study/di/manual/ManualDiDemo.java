package com.gdgku.study.di.manual;

/**
 * [1단계: 직접 조립]
 *
 * 필요한 객체를 전부 new로 직접 만들어서 손으로 연결한다.
 * Driver를 만들려면 Vehicle이 필요하고, Vehicle(Car)을 만들려면 Engine이 필요하다는 것을
 * "사용하는 쪽" 코드(이 클래스)가 전부 알고 있어야 한다.
 *
 * 문제점:
 * - GasEngine을 ElectricEngine으로 바꾸려면 이 조립 코드를 직접 찾아서 고쳐야 한다.
 * - 다른 곳에서도 Car/Driver가 필요하면 이 조립 코드를 그대로 복붙하게 된다.
 * - 즉, "무엇을 쓸지 결정하는 일"과 "그것을 사용하는 일"이 분리되어 있지 않다.
 */
public class ManualDiDemo {

    public static String run() {
        Engine engine = new GasEngine();
        Vehicle car = new Car(engine);
        Driver driver = new Driver(car);
        return driver.startTrip();
    }

    public static void main(String[] args) {
        System.out.println(run());
    }
}

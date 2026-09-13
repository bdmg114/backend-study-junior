package com.gdgku.study.di.manual;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManualDiDemoTest {

    @Test
    void run_직접_조립한_객체_그래프로_주행_결과를_반환한다() {
        String result = ManualDiDemo.run();

        assertEquals("운전자: 휘발유 엔진 시동 -> 출발!", result);
    }

    @Test
    void Car는_생성자로_주입받은_Engine을_그대로_사용한다() {
        Engine electricEngine = new ElectricEngine();
        Car car = new Car(electricEngine);

        assertEquals("전기 모터 구동 -> 출발!", car.drive());
    }
}

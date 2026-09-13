package com.gdgku.study.di.spring;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * @Bean 메서드는 스프링 컨테이너가 없어도 그냥 평범한 메서드로 직접 호출해볼 수 있다.
 * 이 테스트는 스프링 컨텍스트를 전혀 띄우지 않고도 VehicleConfig가 GasEngine을 고른다는
 * 사실과, 파라미터로 받은 Engine을 그대로 Car에 넘긴다는 조립 로직을 검증한다.
 */
class VehicleConfigTest {

    private final VehicleConfig config = new VehicleConfig();

    @Test
    void engine_빈_메서드는_GasEngine을_선택한다() {
        assertInstanceOf(GasEngine.class, config.engine());
    }

    @Test
    void vehicle_빈_메서드는_전달받은_Engine으로_Car를_조립한다() {
        Vehicle vehicle = config.vehicle(new ElectricEngine());

        assertEquals("전기 모터 구동 -> 출발!", vehicle.drive());
    }
}

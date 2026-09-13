package com.gdgku.study.di.container;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleContainerTest {

    @Test
    void register한_타입은_생성자_의존성까지_재귀적으로_조립되어_resolve된다() {
        SimpleContainer container = new SimpleContainer();
        container.register(Engine.class, ElectricEngine.class);
        container.register(Vehicle.class, Car.class);
        container.register(Driver.class, Driver.class);

        Driver driver = container.resolve(Driver.class);

        assertEquals("운전자: 전기 모터 구동 -> 출발!", driver.startTrip());
    }

    @Test
    void 같은_타입을_다시_resolve하면_동일한_싱글턴_인스턴스를_반환한다() {
        SimpleContainer container = new SimpleContainer();
        container.register(Engine.class, GasEngine.class);

        Engine first = container.resolve(Engine.class);
        Engine second = container.resolve(Engine.class);

        assertSame(first, second);
    }

    @Test
    void 등록되지_않은_타입을_resolve하면_예외가_발생한다() {
        SimpleContainer container = new SimpleContainer();

        assertThrows(IllegalStateException.class, () -> container.resolve(Engine.class));
    }

    @Test
    void ContainerDiDemo_run은_컨테이너가_조립한_결과를_반환한다() {
        assertEquals("운전자: 휘발유 엔진 시동 -> 출발!", ContainerDiDemo.run());
    }
}

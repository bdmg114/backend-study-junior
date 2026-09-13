package com.gdgku.study.di.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * [3단계: 스프링 - "무엇을 쓸지"는 조립 담당자가 결정한다]
 *
 * ManualDiDemo.run()이 new GasEngine()을 직접 골랐고, ContainerDiDemo.run()이
 * container.register(Engine.class, GasEngine.class)로 골랐던 것처럼, 어떤 Engine을 쓸지는
 * Car가 아니라 조립을 전담하는 이 설정 클래스가 결정한다. Car가 스스로 @Qualifier로
 * "나는 gasEngine을 쓰겠다"고 정해버리면, Car는 더 이상 Engine이라는 추상화가 아니라
 * 특정 구현체에 의존하는 꼴이 된다.
 *
 * Engine/Vehicle(Car)/Driver는 전부 평범한 자바 클래스(POJO)다 - 어노테이션이 하나도 없다.
 * 스프링이 하는 일은 이 클래스에 적힌 @Bean 메서드들을 의존관계 순서대로 호출해서
 * 그 결과물을 컨테이너에 등록해두는 것뿐이다.
 */
@Configuration
public class VehicleConfig {

    @Bean
    public Engine engine() {
        return new GasEngine();
    }

    @Bean
    public Vehicle vehicle(Engine engine) {
        return new Car(engine);
    }

    @Bean
    public Driver driver(Vehicle vehicle) {
        return new Driver(vehicle);
    }
}

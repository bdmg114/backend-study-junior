package com.gdgku.study.di.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [3단계: 스프링 - 실행(트리거)]
 *
 * ManualDiDemo/ContainerDiDemo는 main()으로 조립과 실행을 한 번에 한다. 스프링에서는
 * 조립(VehicleConfig)과 실행이 분리되어 있다 - 애플리케이션이 시작될 때 VehicleConfig가
 * 이미 Driver까지 다 조립해서 컨테이너에 등록해두고, 이 컨트롤러는 요청이 들어올 때마다
 * 이미 만들어져 있는 Driver 빈을 꺼내 쓰기만 한다. new도, register()도 여기에는 없다.
 */
@RestController
public class SpringDiDemo {

    private final Driver driver;

    public SpringDiDemo(Driver driver) {
        this.driver = driver;
    }

    @GetMapping("/api/di-demo/spring")
    public String run() {
        return driver.startTrip();
    }
}

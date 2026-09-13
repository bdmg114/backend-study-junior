package com.gdgku.study.di.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ManualDiDemoTest/SimpleContainerTest는 static 메서드를 직접 호출해서 검증하면 됐지만,
 * SpringDiDemo는 스프링 빈이라 main()으로 실행할 수 없다 - 실제로 애플리케이션을 띄우고
 * GetMapping으로 등록된 엔드포인트에 요청을 보내야 결과를 확인할 수 있다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class SpringDiDemoTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void spring_엔드포인트는_VehicleConfig가_조립한_Driver의_결과를_반환한다() {
        String body = restTemplate.getForObject("/api/di-demo/spring", String.class);

        assertEquals("운전자: 휘발유 엔진 시동 -> 출발!", body);
    }
}

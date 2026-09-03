package com.gdgku.study.backend.example;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * 학습용 예시 컨트롤러 (카페 주문 API).
 *
 * 숫자 ID 기반 CRUD의 전형적인 형태를 담았습니다.
 * MemoController 를 직접 구현할 때 참고용으로만 보세요 — 복붙 대상이 아닙니다.
 *
 * 이 클래스도 @RestController 이므로 앱을 띄우면 실제로 /orders 가 동작합니다.
 * 다 익히고 나면 파일째 지우면 됩니다.
 */
@RestController
@RequestMapping("/orders") // 클래스 레벨에 공통 경로를 두면 메서드마다 반복하지 않아도 됩니다
public class OrderController {

    // ---------------------------------------------------------------
    // 1. 타입 정의
    //    실제 프로젝트에서는 각각 별도 .java 파일로 분리하는 것이 관례입니다.
    //    (중첩 record 는 바깥 클래스에서 private 필드에 직접 접근할 수 있어서,
    //     .menu() 대신 .menu 로 써도 컴파일되는 함정이 있습니다. 분리하면 그 유혹이 사라집니다.)
    // ---------------------------------------------------------------

    /** 클라이언트 -> 서버. id 가 없다는 점이 핵심입니다. id 는 서버가 정합니다. */
    public record OrderRequest(
            @NotBlank(message = "메뉴명은 필수입니다")
            String menu,

            @NotBlank(message = "요청사항은 필수입니다")
            String note
    ) {
    }

    /** 서버 내부 저장 + 클라이언트로 나가는 응답. id 를 가집니다. */
    public record Order(Long id, String menu, String note) {
    }

    // ---------------------------------------------------------------
    // 2. 저장소와 ID 시퀀스
    // ---------------------------------------------------------------

    /**
     * 컨트롤러는 싱글턴이고 요청마다 스레드가 다르므로,
     * 여러 스레드가 동시에 건드리는 맵은 ConcurrentHashMap 이어야 안전합니다.
     * HashMap 을 쓰면 순회 도중 put 이 일어날 때 ConcurrentModificationException 이 납니다.
     */
    private final Map<Long, Order> store = new ConcurrentHashMap<>();

    /**
     * long 필드에 ++ 를 쓰면 안 됩니다. 읽기-더하기-쓰기 3단계라 원자적이지 않고,
     * 동시 요청 두 건이 같은 ID 를 받아 서로를 덮어쓸 수 있습니다.
     * AtomicLong 은 이 3단계를 CPU 의 CAS 명령 하나로 처리합니다.
     */
    private final AtomicLong sequence = new AtomicLong(0);

    // ---------------------------------------------------------------
    // 3. 초기 데이터 적재
    // ---------------------------------------------------------------

    public OrderController() {
        Map<String, String> seed = Map.ofEntries(
                Map.entry("아메리카노", "샷 추가"),
                Map.entry("카페라떼", "오트밀크로 변경"),
                Map.entry("콜드브루", "얼음 적게")
        );
        // Map.forEach 는 (key, value) 두 인자를 넘기고, save(String, String) 시그니처와 맞아떨어집니다.
        seed.forEach(this::save);
    }

    /**
     * ID 발급과 저장을 한 곳에 모은 헬퍼.
     * 초기 데이터 적재와 POST 핸들러가 이 메서드를 함께 쓰므로
     * "ID 를 만드는 코드"가 프로젝트에 단 하나만 존재하게 됩니다.
     */
    private Order save(String menu, String note) {
        long id = sequence.incrementAndGet();
        Order order = new Order(id, menu, note);
        store.put(id, order);
        return order;
    }

    // ---------------------------------------------------------------
    // 4. 엔드포인트
    // ---------------------------------------------------------------

    /** GET /orders - 랜덤으로 한 건. */
    @GetMapping
    public ResponseEntity<Order> readRandom() {
        List<Order> all = new ArrayList<>(store.values());

        // nextInt(0) 은 IllegalArgumentException 을 던지므로 빈 경우를 먼저 걸러냅니다.
        if (all.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }

        Order picked = all.get(ThreadLocalRandom.current().nextInt(all.size()));
        return ResponseEntity.ok(picked); // 200
    }

    /**
     * GET /orders/{id} - 특정 한 건.
     *
     * 경로의 {id} 이름과 @PathVariable 파라미터 이름이 같아야 연결됩니다.
     * 숫자가 아닌 값(/orders/abc)이 오면 Spring 이 변환에 실패해 400 을 자동으로 냅니다.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> readOne(@PathVariable Long id) {
        Order found = store.get(id);

        if (found == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.ok(found); // 200
    }

    /**
     * POST /orders - 새로 생성.
     *
     * @Valid 를 붙여야 OrderRequest 의 @NotBlank 가 실제로 검사됩니다.
     * 애노테이션만 달고 @Valid 를 빠뜨리면 아무 검증도 일어나지 않습니다.
     * 검증에 실패하면 Spring 이 400 Bad Request 를 대신 내줍니다.
     */
    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody OrderRequest request) {
        Order saved = save(request.menu(), request.note());

        // created(uri) 는 201 상태 코드와 Location 헤더를 한 번에 설정합니다.
        // Location 은 "방금 만든 리소스는 여기 있다"를 알려주는 관례적인 헤더입니다.
        return ResponseEntity
                .created(URI.create("/orders/" + saved.id()))
                .body(saved);
    }

    /**
     * DELETE /orders/{id} - 삭제.
     *
     * Map.remove 는 지워진 값을 반환하고, 키가 없었으면 null 을 반환합니다.
     * 이 반환값 하나로 "실제로 지웠는지"를 판별할 수 있어 containsKey 를 따로 부를 필요가 없습니다.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Order removed = store.remove(id);

        if (removed == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        // 204 No Content: 성공했고 돌려줄 본문이 없다는 뜻. 삭제 성공의 가장 일반적인 응답입니다.
        return ResponseEntity.noContent().build();
    }
}

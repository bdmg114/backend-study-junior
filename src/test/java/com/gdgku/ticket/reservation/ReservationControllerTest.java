package com.gdgku.ticket.reservation;

import com.gdgku.ticket.concert.Concert;
import com.gdgku.ticket.concert.ConcertRepository;
import com.gdgku.ticket.reservation.dto.ReservationRequest;
import com.gdgku.ticket.reservation.dto.ReservationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 예매(Reservation) 기능에서 실제로 터질 수 있는 문제 상황들을 검증한다.
 * (좌석 초과 예매, 존재하지 않는 리소스 접근, 잘못된 입력값, 중복 취소, 동시성 이슈 등)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ReservationControllerTest {

    private static final int TOTAL_SEATS = 3;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Long concertId;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        concertRepository.deleteAll();

        Concert concert = new Concert(
                "IU Concert",
                "IU",
                "Jamsil Stadium",
                LocalDateTime.now().plusDays(30),
                TOTAL_SEATS,
                150000
        );
        concertId = concertRepository.save(concert).getId();
    }

    private ReservationRequest createValidRequest(int seatCount) {
        ReservationRequest request = new ReservationRequest();
        request.setReserverName("Gildong Hong");
        request.setReserverEmail("hong@test.com");
        request.setSeatCount(seatCount);
        return request;
    }

    @Test
    void createReservation_성공하면_201을_반환하고_잔여좌석이_줄어든다() {
        ResponseEntity<ReservationResponse> response = restTemplate.postForEntity(
                "/api/concerts/" + concertId + "/reservations", createValidRequest(2), ReservationResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("CONFIRMED", response.getBody().getStatus().name());

        Concert concert = concertRepository.findById(concertId).orElseThrow();
        assertEquals(1, concert.getAvailableSeats());
    }

    @Test
    void createReservation_잔여좌석보다_많이_예매하면_409를_반환하고_좌석수는_변하지_않는다() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/concerts/" + concertId + "/reservations", createValidRequest(TOTAL_SEATS + 1), String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().contains("NOT_ENOUGH_SEATS"));

        Concert concert = concertRepository.findById(concertId).orElseThrow();
        assertEquals(TOTAL_SEATS, concert.getAvailableSeats());
    }

    @Test
    void createReservation_잔여좌석을_정확히_모두_예매하면_성공하고_이후_예매는_실패한다() {
        ResponseEntity<ReservationResponse> first = restTemplate.postForEntity(
                "/api/concerts/" + concertId + "/reservations", createValidRequest(TOTAL_SEATS), ReservationResponse.class);
        assertEquals(HttpStatus.CREATED, first.getStatusCode());

        Concert concert = concertRepository.findById(concertId).orElseThrow();
        assertEquals(0, concert.getAvailableSeats());

        ResponseEntity<String> second = restTemplate.postForEntity(
                "/api/concerts/" + concertId + "/reservations", createValidRequest(1), String.class);
        assertEquals(HttpStatus.CONFLICT, second.getStatusCode());
    }

    @Test
    void createReservation_존재하지_않는_콘서트면_404를_반환한다() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/concerts/999/reservations", createValidRequest(1), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("CONCERT_NOT_FOUND"));
    }

    @Test
    void createReservation_좌석수가_0이하이면_400을_반환한다() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/concerts/" + concertId + "/reservations", createValidRequest(0), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("INVALID_INPUT"));
    }

    @Test
    void createReservation_이메일형식이_올바르지_않으면_400을_반환한다() {
        ReservationRequest request = createValidRequest(1);
        request.setReserverEmail("not-an-email");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/concerts/" + concertId + "/reservations", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("reserverEmail"));
    }

    @Test
    void createReservation_예매자_이름이_비어있으면_400을_반환한다() {
        ReservationRequest request = createValidRequest(1);
        request.setReserverName("");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/concerts/" + concertId + "/reservations", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getReservationsByConcert_존재하지_않는_콘서트면_404를_반환한다() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/api/concerts/999/reservations", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getReservation_존재하지_않으면_404를_반환한다() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/api/reservations/999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("RESERVATION_NOT_FOUND"));
    }

    @Test
    void cancelReservation_취소하면_잔여좌석이_복구된다() {
        ReservationResponse reservation = restTemplate.postForEntity(
                "/api/concerts/" + concertId + "/reservations", createValidRequest(2), ReservationResponse.class).getBody();

        restTemplate.delete("/api/reservations/" + reservation.getId());

        Concert concert = concertRepository.findById(concertId).orElseThrow();
        assertEquals(TOTAL_SEATS, concert.getAvailableSeats());

        Reservation cancelled = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertEquals(ReservationStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    void cancelReservation_이미_취소된_예매를_다시_취소하면_409를_반환한다() {
        ReservationResponse reservation = restTemplate.postForEntity(
                "/api/concerts/" + concertId + "/reservations", createValidRequest(1), ReservationResponse.class).getBody();
        restTemplate.delete("/api/reservations/" + reservation.getId());

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/reservations/" + reservation.getId(),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().contains("RESERVATION_ALREADY_CANCELLED"));
    }

    @Test
    void cancelReservation_존재하지_않으면_404를_반환한다() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/reservations/999",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * 동시성 문제(overbooking) 재현 테스트.
     * ReservationService.createReservation()은 "잔여 좌석 확인"과 "좌석 수 반영"이
     * 하나의 원자적 연산으로 묶여 있지 않기 때문에, 여러 요청이 동시에 마지막 남은 좌석을
     * 예매하면 총 좌석 수보다 많은 예매가 성공해버리는 초과 예매(overbooking)가 발생할 수 있다.
     * 이 테스트는 그 문제를 드러내기 위한 것으로, 비관적 락(@Lock)이나 낙관적 락(@Version) 등의
     * 동시성 제어가 추가되지 않는 한 실패할 수 있다.
     */
    // @Disabled("""
    //         현재 ReservationService는 '잔여 좌석 확인'과 '좌석 수 반영'을 하나의 원자적 연산으로 처리하지 않아서 \
    //         여러 요청이 동시에 들어오면 실제로 초과 예매가 재현된다 (== 이 테스트는 실패한다).
    //         비관적 락(@Lock(PESSIMISTIC_WRITE))이나 낙관적 락(@Version) + 재시도 로직을 추가해 \
    //         동시성 문제를 해결한 뒤 이 애노테이션을 제거하고 테스트를 통과시켜 보자.
    //         """)
    @Test
    void createReservation_동시에_요청이_몰리면_좌석수를_초과하는_예매가_발생할_수_있다() throws InterruptedException {
        int requestCount = 10; // 좌석은 3석뿐인데 10명이 동시에 1석씩 예매를 시도한다.
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        CountDownLatch latch = new CountDownLatch(requestCount);
        AtomicInteger successCount = new AtomicInteger();

        for (int i = 0; i < requestCount; i++) {
            executorService.submit(() -> {
                try {
                    ResponseEntity<ReservationResponse> response = restTemplate.postForEntity(
                            "/api/concerts/" + concertId + "/reservations", createValidRequest(1), ReservationResponse.class);
                    if (response.getStatusCode() == HttpStatus.CREATED) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        Concert concert = concertRepository.findById(concertId).orElseThrow();

        // 좌석 수 검증과 반영 사이의 race condition으로 인해
        // 성공한 예매 수가 실제 좌석 수(3)를 초과할 수 있다.
        assertTrue(successCount.get() <= TOTAL_SEATS,
                "동시 요청으로 인해 총 좌석 수(" + TOTAL_SEATS + ")보다 많은 예매(" + successCount.get() + "건)가 성공했습니다.");
        assertTrue(concert.getReservedSeats() <= TOTAL_SEATS,
                "예약된 좌석 수(" + concert.getReservedSeats() + ")가 총 좌석 수(" + TOTAL_SEATS + ")를 초과했습니다.");
    }
}

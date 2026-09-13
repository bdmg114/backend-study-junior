package com.gdgku.ticket.concert;

import com.gdgku.ticket.concert.dto.ConcertRequest;
import com.gdgku.ticket.concert.dto.ConcertResponse;
import com.gdgku.ticket.reservation.ReservationRepository;
import com.gdgku.ticket.reservation.dto.ReservationRequest;
import com.gdgku.ticket.reservation.dto.ReservationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 콘서트(Concert) 리소스에 대한 일반적인 CRUD 시나리오를 검증한다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ConcertControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        concertRepository.deleteAll();
    }

    private ConcertRequest createValidRequest() {
        ConcertRequest request = new ConcertRequest();
        request.setTitle("IU Concert");
        request.setArtist("IU");
        request.setVenue("Jamsil Stadium");
        request.setConcertAt(LocalDateTime.now().plusDays(30));
        request.setTotalSeats(100);
        request.setPrice(150000);
        return request;
    }

    @Test
    void createConcert_성공하면_201과_생성된_콘서트를_반환한다() {
        ConcertRequest request = createValidRequest();

        ResponseEntity<ConcertResponse> response =
                restTemplate.postForEntity("/api/concerts", request, ConcertResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("IU Concert", response.getBody().getTitle());
        assertEquals(100, response.getBody().getAvailableSeats());
        assertEquals(0, response.getBody().getReservedSeats());
    }

    @Test
    void createConcert_필수값이_비어있으면_400을_반환한다() {
        ConcertRequest request = createValidRequest();
        request.setTitle("");

        ResponseEntity<String> response =
                restTemplate.postForEntity("/api/concerts", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("INVALID_INPUT"));
    }

    @Test
    void createConcert_공연일시가_과거이면_400을_반환한다() {
        ConcertRequest request = createValidRequest();
        request.setConcertAt(LocalDateTime.now().minusDays(1));

        ResponseEntity<String> response =
                restTemplate.postForEntity("/api/concerts", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getConcerts_전체_콘서트_목록을_조회한다() {
        restTemplate.postForEntity("/api/concerts", createValidRequest(), ConcertResponse.class);
        restTemplate.postForEntity("/api/concerts", createValidRequest(), ConcertResponse.class);

        ResponseEntity<ConcertResponse[]> response =
                restTemplate.getForEntity("/api/concerts", ConcertResponse[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().length);
    }

    @Test
    void getConcert_존재하는_콘서트를_조회한다() {
        ConcertResponse created =
                restTemplate.postForEntity("/api/concerts", createValidRequest(), ConcertResponse.class).getBody();

        ResponseEntity<ConcertResponse> response =
                restTemplate.getForEntity("/api/concerts/" + created.getId(), ConcertResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(created.getId(), response.getBody().getId());
        assertEquals("IU Concert", response.getBody().getTitle());
    }

    @Test
    void getConcert_존재하지_않으면_404와_에러코드를_반환한다() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/api/concerts/999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("CONCERT_NOT_FOUND"));
    }

    @Test
    void updateConcert_성공하면_수정된_정보를_반환한다() {
        ConcertResponse created =
                restTemplate.postForEntity("/api/concerts", createValidRequest(), ConcertResponse.class).getBody();

        ConcertRequest updateRequest = createValidRequest();
        updateRequest.setTitle("BTS Concert");
        updateRequest.setPrice(200000);

        ResponseEntity<ConcertResponse> response = restTemplate.exchange(
                "/api/concerts/" + created.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                ConcertResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("BTS Concert", response.getBody().getTitle());
        assertEquals(200000, response.getBody().getPrice());
    }

    @Test
    void updateConcert_존재하지_않으면_404를_반환한다() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/concerts/999",
                HttpMethod.PUT,
                new HttpEntity<>(createValidRequest()),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateConcert_이미_예매된_좌석수보다_총좌석수를_적게_수정하면_400을_반환한다() {
        ConcertRequest createRequest = createValidRequest();
        createRequest.setTotalSeats(100);
        ConcertResponse created =
                restTemplate.postForEntity("/api/concerts", createRequest, ConcertResponse.class).getBody();

        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setReserverName("Gildong Hong");
        reservationRequest.setReserverEmail("hong@test.com");
        reservationRequest.setSeatCount(50);
        restTemplate.postForEntity(
                "/api/concerts/" + created.getId() + "/reservations", reservationRequest, ReservationResponse.class);

        ConcertRequest updateRequest = createValidRequest();
        updateRequest.setTotalSeats(10);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/concerts/" + created.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("INVALID_TOTAL_SEATS"));
    }

    @Test
    void deleteConcert_예매내역이_없으면_삭제된다() {
        ConcertResponse created =
                restTemplate.postForEntity("/api/concerts", createValidRequest(), ConcertResponse.class).getBody();

        restTemplate.delete("/api/concerts/" + created.getId());

        ResponseEntity<String> response =
                restTemplate.getForEntity("/api/concerts/" + created.getId(), String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteConcert_존재하지_않으면_404를_반환한다() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/concerts/999",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteConcert_예매내역이_있으면_409를_반환하고_삭제되지_않는다() {
        ConcertResponse created =
                restTemplate.postForEntity("/api/concerts", createValidRequest(), ConcertResponse.class).getBody();

        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setReserverName("Gildong Hong");
        reservationRequest.setReserverEmail("hong@test.com");
        reservationRequest.setSeatCount(1);
        restTemplate.postForEntity(
                "/api/concerts/" + created.getId() + "/reservations", reservationRequest, ReservationResponse.class);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/concerts/" + created.getId(),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().contains("CONCERT_HAS_RESERVATIONS"));

        ResponseEntity<ConcertResponse> stillExists =
                restTemplate.getForEntity("/api/concerts/" + created.getId(), ConcertResponse.class);
        assertEquals(HttpStatus.OK, stillExists.getStatusCode());
    }
}

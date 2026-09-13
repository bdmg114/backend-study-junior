package com.gdgku.ticket.reservation;

import com.gdgku.ticket.reservation.dto.ReservationRequest;
import com.gdgku.ticket.reservation.dto.ReservationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/concerts/{concertId}/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@PathVariable Long concertId,
                                                                   @Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(concertId, request);
        return ResponseEntity.created(URI.create("/api/reservations/" + response.getId())).body(response);
    }

    @GetMapping("/concerts/{concertId}/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservationsByConcert(@PathVariable Long concertId) {
        return ResponseEntity.ok(reservationService.getReservationsByConcert(concertId));
    }

    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.getReservation(reservationId));
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}

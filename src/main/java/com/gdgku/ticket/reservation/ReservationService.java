package com.gdgku.ticket.reservation;

import com.gdgku.ticket.common.exception.ErrorCode;
import com.gdgku.ticket.common.exception.TicketException;
import com.gdgku.ticket.concert.Concert;
import com.gdgku.ticket.concert.ConcertRepository;
import com.gdgku.ticket.reservation.dto.ReservationRequest;
import com.gdgku.ticket.reservation.dto.ReservationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ConcertRepository concertRepository;

    public ReservationService(ReservationRepository reservationRepository, ConcertRepository concertRepository) {
        this.reservationRepository = reservationRepository;
        this.concertRepository = concertRepository;
    }

    @Transactional
    public ReservationResponse createReservation(Long concertId, ReservationRequest request) {
        Concert concert = findConcertOrThrow(concertId);

        int seatCount = request.getSeatCount();
        if (concert.getAvailableSeats() < seatCount) {
            throw new TicketException(ErrorCode.NOT_ENOUGH_SEATS);
        }
        concert.setReservedSeats(concert.getReservedSeats() + seatCount);

        Reservation reservation = new Reservation(
                concert,
                request.getReserverName(),
                request.getReserverEmail(),
                seatCount
        );
        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(savedReservation);
    }

    public List<ReservationResponse> getReservationsByConcert(Long concertId) {
        findConcertOrThrow(concertId);

        List<Reservation> reservations = reservationRepository.findByConcertId(concertId);
        List<ReservationResponse> responses = new ArrayList<>();
        for (Reservation reservation : reservations) {
            responses.add(new ReservationResponse(reservation));
        }
        return responses;
    }

    public ReservationResponse getReservation(Long reservationId) {
        Reservation reservation = findReservationOrThrow(reservationId);
        return new ReservationResponse(reservation);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = findReservationOrThrow(reservationId);

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new TicketException(ErrorCode.RESERVATION_ALREADY_CANCELLED);
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        Concert concert = reservation.getConcert();
        concert.setReservedSeats(concert.getReservedSeats() - reservation.getSeatCount());
    }

    private Concert findConcertOrThrow(Long concertId) {
        return concertRepository.findById(concertId)
                .orElseThrow(() -> new TicketException(ErrorCode.CONCERT_NOT_FOUND));
    }

    private Reservation findReservationOrThrow(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new TicketException(ErrorCode.RESERVATION_NOT_FOUND));
    }
}

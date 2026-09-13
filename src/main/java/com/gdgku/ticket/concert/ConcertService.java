package com.gdgku.ticket.concert;

import com.gdgku.ticket.common.exception.ErrorCode;
import com.gdgku.ticket.common.exception.TicketException;
import com.gdgku.ticket.concert.dto.ConcertRequest;
import com.gdgku.ticket.concert.dto.ConcertResponse;
import com.gdgku.ticket.reservation.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ReservationRepository reservationRepository;

    public ConcertService(ConcertRepository concertRepository, ReservationRepository reservationRepository) {
        this.concertRepository = concertRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ConcertResponse createConcert(ConcertRequest request) {
        Concert concert = new Concert(
                request.getTitle(),
                request.getArtist(),
                request.getVenue(),
                request.getConcertAt(),
                request.getTotalSeats(),
                request.getPrice()
        );
        Concert savedConcert = concertRepository.save(concert);
        return new ConcertResponse(savedConcert);
    }

    public List<ConcertResponse> getConcerts() {
        List<Concert> concerts = concertRepository.findAll();
        List<ConcertResponse> responses = new ArrayList<>();
        for (Concert concert : concerts) {
            responses.add(new ConcertResponse(concert));
        }
        return responses;
    }

    public ConcertResponse getConcert(Long concertId) {
        Concert concert = findConcertOrThrow(concertId);
        return new ConcertResponse(concert);
    }

    @Transactional
    public ConcertResponse updateConcert(Long concertId, ConcertRequest request) {
        Concert concert = findConcertOrThrow(concertId);

        if (request.getTotalSeats() < concert.getReservedSeats()) {
            throw new TicketException(ErrorCode.INVALID_TOTAL_SEATS);
        }

        concert.setTitle(request.getTitle());
        concert.setArtist(request.getArtist());
        concert.setVenue(request.getVenue());
        concert.setConcertAt(request.getConcertAt());
        concert.setTotalSeats(request.getTotalSeats());
        concert.setPrice(request.getPrice());

        return new ConcertResponse(concert);
    }

    @Transactional
    public void deleteConcert(Long concertId) {
        Concert concert = findConcertOrThrow(concertId);

        if (!reservationRepository.findByConcertId(concertId).isEmpty()) {
            throw new TicketException(ErrorCode.CONCERT_HAS_RESERVATIONS);
        }

        concertRepository.delete(concert);
    }

    private Concert findConcertOrThrow(Long concertId) {
        return concertRepository.findById(concertId)
                .orElseThrow(() -> new TicketException(ErrorCode.CONCERT_NOT_FOUND));
    }
}

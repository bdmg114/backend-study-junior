package com.gdgku.ticket.concert;

import com.gdgku.ticket.concert.dto.ConcertRequest;
import com.gdgku.ticket.concert.dto.ConcertResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/concerts")
public class ConcertController {

    private final ConcertService concertService;

    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    @PostMapping
    public ResponseEntity<ConcertResponse> createConcert(@Valid @RequestBody ConcertRequest request) {
        ConcertResponse response = concertService.createConcert(request);
        return ResponseEntity.created(URI.create("/api/concerts/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ConcertResponse>> getConcerts() {
        return ResponseEntity.ok(concertService.getConcerts());
    }

    @GetMapping("/{concertId}")
    public ResponseEntity<ConcertResponse> getConcert(@PathVariable Long concertId) {
        return ResponseEntity.ok(concertService.getConcert(concertId));
    }

    @PutMapping("/{concertId}")
    public ResponseEntity<ConcertResponse> updateConcert(@PathVariable Long concertId,
                                                           @Valid @RequestBody ConcertRequest request) {
        return ResponseEntity.ok(concertService.updateConcert(concertId, request));
    }

    @DeleteMapping("/{concertId}")
    public ResponseEntity<Void> deleteConcert(@PathVariable Long concertId) {
        concertService.deleteConcert(concertId);
        return ResponseEntity.noContent().build();
    }
}

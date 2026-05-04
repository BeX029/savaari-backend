package com.savaari.backend.controller;

import com.savaari.backend.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping
    public ResponseEntity<?> submitRating(@RequestBody Map<String, Object> body) {
        Long fromUserId = ((Number) body.get("fromUserId")).longValue();
        Long toUserId   = ((Number) body.get("toUserId")).longValue();
        Long bookingId  = ((Number) body.get("bookingId")).longValue();
        int  stars      = ((Number) body.get("stars")).intValue();
        String comment  = (String) body.getOrDefault("comment", "");
        ratingService.submitRating(bookingId, fromUserId, toUserId, stars, comment);
        return ResponseEntity.ok(Map.of("message", "Rating submitted!"));
    }
}
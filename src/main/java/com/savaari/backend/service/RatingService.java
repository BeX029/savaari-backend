package com.savaari.backend.service;

import com.savaari.backend.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    public void submitRating(Long bookingId, Long fromUserId,
                             Long toUserId, int stars, String comment) {
        ratingRepository.insertRating(bookingId, fromUserId, toUserId, stars, comment);
        ratingRepository.updateDriverAvgRating(toUserId);
    }
}
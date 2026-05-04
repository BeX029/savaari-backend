package com.savaari.backend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RatingRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public void insertRating(Long bookingId, Long fromUserId,
                             Long toUserId, int stars, String comment) {
        jdbc.update("EXEC sp_RateDriver ?, ?, ?, ?, ?",
                bookingId, fromUserId, toUserId, stars, comment);
    }

    public void updateDriverAvgRating(Long toUserId) {
        jdbc.update(
            "UPDATE Drivers SET AvgRating = " +
            "(SELECT AVG(CAST(Stars AS DECIMAL(3,2))) FROM Ratings WHERE ToUserID = ?) " +
            "WHERE UserID = ?", toUserId, toUserId);
    }
}
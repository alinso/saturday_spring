package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Review;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {


    @Query("select review from Review review where review.writer=:me and review.reader=:other")
    Review myPreviousReview(@Param("me") User me, @Param("other")User other);

    List<Review> findByReader(User reader);

    @Query("select review from Review review where review.reader=:reader and review.createdAt<:twoDaysAgo")
    List<Review> findByReaderBefore2Days(@Param("reader")User reader, @Param("twoDaysAgo") Date twoDaysAgo);

    @Query("select review from Review review where review.writer=:writer and review.createdAt>:threeMonthsAgo")
    List<Review> last3MonthReviewsOfUser(@Param("writer") User user, @Param("threeMonthsAgo") Date threeMonthsAgo);
}

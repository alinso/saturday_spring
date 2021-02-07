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


    @Query("select review from Review review where review.writer=:me and review.reader=:other " +
            " and review.writer.enabled=true and review.reader.enabled=true ")
    Review myPreviousReview(@Param("me") User me, @Param("other") User other);

    @Query("select  r from Review  r where r.reader=:reader and r.writer.enabled=true and r.reader.enabled=true")
    List<Review> findByReader(@Param("reader") User reader);

    @Query("select review from Review review where review.writer=:writer and review.reader.enabled=true")
    List<Review> last3MonthReviewsOfUser(@Param("writer") User user);
}

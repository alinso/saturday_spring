package com.alinso.myapp.service;

import com.alinso.myapp.dto.review.ReviewDto;
import com.alinso.myapp.entity.Review;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.ReviewType;
import com.alinso.myapp.repository.ReviewRepository;
import com.alinso.myapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ModelMapper modelMapper;

    public void writeAsFriend(ReviewDto reviewDto){
        User reader =userRepository.findById(reviewDto.getReader().getId()).get();
        User writer  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Review review =modelMapper.map(reviewDto, Review.class);

        review.setWriter(writer);
        review.setReader(reader);
        review.setReviewType(ReviewType.FRIEND);

        reviewRepository.save(review);

    }

}

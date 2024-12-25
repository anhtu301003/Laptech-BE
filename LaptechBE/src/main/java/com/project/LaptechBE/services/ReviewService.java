package com.project.LaptechBE.services;

import com.project.LaptechBE.DTO.ReviewDTO.ReviewDTO;
import com.project.LaptechBE.models.Product;
import com.project.LaptechBE.models.Review;
import com.project.LaptechBE.repositories.ProductRepository;
import com.project.LaptechBE.repositories.ReviewRepository;
import com.project.LaptechBE.repositories.UserRepository;
import com.project.LaptechBE.services.IServices.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public Object addReview(String userId,ReviewDTO reviewDTO) {
        try{
            var UserOpt = userRepository.findById(userId);

            var ProductOpt = productRepository.findById(reviewDTO.getProductId());

            Review review = Review.builder()
                    .userId(UserOpt.get())
                    .productId(ProductOpt.get())
                    .rating(reviewDTO.getRating())
                    .comment(reviewDTO.getComment())
                    .build();

            reviewRepository.save(review);

            var reviews = reviewRepository.findByProductId(reviewDTO.getProductId());
            var averageRating = reviews.stream().mapToDouble(
                    item -> item.getRating()
            ).average();

            Product product = ProductOpt.get();

            product.setAverageRating(averageRating.isPresent() ? averageRating.getAsDouble() : 0);

            productRepository.save(product);
            return review;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getReviewsByProduct(String productId) {
        return null;
    }

    @Override
    public Object getReviewsByUser(String userId) {
        return null;
    }

    @Override
    public Object updateReview(String reviewId, String userId, int rating, String comment) {
        return null;
    }

    @Override
    public Object deleteReview(String reviewId, String userId) {
        return null;
    }
}

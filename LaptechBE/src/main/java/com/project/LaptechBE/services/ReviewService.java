package com.project.LaptechBE.services;

import com.project.LaptechBE.DTO.ReviewDTO.ReviewDTO;
import com.project.LaptechBE.models.Product;
import com.project.LaptechBE.models.Review;
import com.project.LaptechBE.models.submodels.submodelsProduct.ProductReview;
import com.project.LaptechBE.repositories.ProductRepository;
import com.project.LaptechBE.repositories.ReviewRepository;
import com.project.LaptechBE.repositories.UserRepository;
import com.project.LaptechBE.services.IServices.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

            var ProductOpt = productRepository.findById(reviewDTO.getProductId().getId().toString());

            Review review = Review.builder()
                    .userId(UserOpt.get())
                    .productId(ProductOpt.get())
                    .rating(reviewDTO.getRating())
                    .comment(reviewDTO.getComment())
                    .build();

            reviewRepository.save(review);


            var reviews = reviewRepository.findByProductId(reviewDTO.getProductId().getId().toString());


            List<ProductReview> Reviews = new ArrayList<>();
            Reviews = ProductOpt.get().getReviews();
            Reviews.add(
                    ProductReview.builder()
                            .userId(review.getUserId())
                            .rating(review.getRating())
                            .comment(review.getComment())
                            .build()
            );

            Product product = ProductOpt.get();

            var averageRating = product.getReviews().stream().mapToDouble(
                    item -> item.getRating().doubleValue()
            ).average();

            product.setReviews(Reviews);

            product.setAverageRating(averageRating.isPresent() ? averageRating.getAsDouble() : 0);

            productRepository.save(product);
            return review;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getReviewsByProduct(String productId) {
        var result = reviewRepository.findByProductId(productId);
        return result;
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

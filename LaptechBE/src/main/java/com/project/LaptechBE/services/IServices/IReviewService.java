package com.project.LaptechBE.services.IServices;

import com.project.LaptechBE.DTO.ReviewDTO.ReviewDTO;

public interface IReviewService {
    public Object addReview(String userId,ReviewDTO reviewDTO);
    public Object getReviewsByProduct(String productId);
    public Object getReviewsByUser(String userId);
    public Object updateReview(String reviewId,String userId,int rating,String comment);
    public Object deleteReview(String reviewId,String userId);
}

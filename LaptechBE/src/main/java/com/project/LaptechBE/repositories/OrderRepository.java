package com.project.LaptechBE.repositories;

import com.project.LaptechBE.models.Cart;
import com.project.LaptechBE.models.Order;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Objects;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Page<Order> findByUserIdAndStatus(ObjectId userId, String status, Pageable pageable);

    // Tìm đơn hàng theo userId, hỗ trợ phân trang
    Page<Order> findByUserId(String userId, Pageable pageable);

    // Tìm đơn hàng theo nhiều tham số, hỗ trợ phân trang
    Page<Order> findByStatusAndPaymentStatusAndCreatedAtBetweenAndShippingAddressFullNameLikeOrTrackingNumberLikeOrCouponCodeLike(
            String status,
            String paymentStatus,
            Date startDate,
            Date endDate,
            String search,
            Pageable pageable
    );

    // Tìm đơn hàng theo nhiều tham số, hỗ trợ phân trang mà không có search
    Page<Order> findByStatusAndPaymentStatusAndCreatedAtBetween(
            String status,
            String paymentStatus,
            Date startDate,
            Date endDate,
            Pageable pageable
    );

    // Tìm đơn hàng không có search
    Page<Order> findByStatusAndPaymentStatus(String status, String paymentStatus, Pageable pageable);
}

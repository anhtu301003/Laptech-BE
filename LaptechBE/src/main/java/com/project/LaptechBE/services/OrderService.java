package com.project.LaptechBE.services;

import com.project.LaptechBE.DTO.OrderDTO.OrderDTO;
import com.project.LaptechBE.enums.PaymentMethodEnum;
import com.project.LaptechBE.enums.StatusEnum;
import com.project.LaptechBE.enums.StatusOrderEnum;
import com.project.LaptechBE.models.Cart;
import com.project.LaptechBE.models.Order;
import com.project.LaptechBE.models.Product;
import com.project.LaptechBE.models.submodels.submodelsOrder.OrderItem;
import com.project.LaptechBE.repositories.CartRepository;
import com.project.LaptechBE.repositories.OrderRepository;
import com.project.LaptechBE.repositories.ProductRepository;
import com.project.LaptechBE.repositories.UserRepository;
import com.project.LaptechBE.services.IServices.IOrderService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final MongoTemplate mongoTemplate;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final CartRepository cartRepository;


    @Async
    public CompletableFuture<Product> getProductAsync(String productId){
        return CompletableFuture.supplyAsync(
                () -> {
                    Optional<Product> productOpt = productRepository.findById(productId);
                    if(productOpt.isEmpty()){
                        throw new RuntimeException("Product not found with id: " + productId);
                    }
                    return productOpt.get();
                }
        );
    }

    @Override
    public Object createOrder(OrderDTO orderDTO, String userId){
        try{
            // List of CompletableFuture to handle async product fetch
            List<CompletableFuture<OrderItem>> itemFutures = new ArrayList<>();

            for (OrderItem orderItem : orderDTO.getItems()) {
                // Asynchronously fetch product details
                CompletableFuture<Product> productFuture = getProductAsync(orderItem.getProductId().getId().toString());

                // Process the product once fetched and calculate the order item
                itemFutures.add(productFuture.thenApplyAsync(product -> {
                    return OrderItem.builder()
                            .ProductId(product)
                            .quantity(orderItem.getQuantity())
                            .build();
                }));
            }

            List<OrderItem> items = new ArrayList<>();
            for (CompletableFuture<OrderItem> future : itemFutures) {
                items.add(future.join()); // Blocks and waits for result
            }

            // Calculate subtotal and total
            double subtotal = items.stream()
                    .mapToDouble(item -> item.getProductId().getPrice().intValue() * item.getQuantity())
                    .sum();

            double total = subtotal;

            var user = userRepository.findById(userId);

            Order order = Order.builder()
                    .userId(user.get())
                    .shippingAddress(orderDTO.getShippingAddress())
                    .paymentMethod(PaymentMethodEnum.valueOf(orderDTO.getPaymentMethod()))
                    .notes(orderDTO.getNotes())
                    .items(
                            items.stream().map(
                                    item -> OrderItem.builder()
                                            .id(new ObjectId())
                                            .name(item.getProductId().getName())
                                            .ProductId(item.getProductId())
                                            .price(item.getProductId().getPrice())
                                            .quantity(item.getQuantity())
                                            .images(item.getProductId().getImages())
                                            .build()
                            ).collect(Collectors.toList())
                    )
                    .couponCode(orderDTO.getCouponCode())
                    .subtotal(subtotal)
                    .total(total)
                    .status(StatusOrderEnum.pending)
                    .build();

            Cart cart = cartRepository.findByUserIdAndStatus(new ObjectId(userId), StatusEnum.active.toString());

            orderRepository.save(order);

            cartRepository.delete(cart);

            return order;
        }catch (Exception e){
            return e.getMessage();
        }
    }

    @Override
    public Object getUserOrders(String userId, Map<String, Object> filter){
//        try{
//                // Tạo Pageable để phân trang
//                Pageable pageable = PageRequest.of((int)filter.get("page") - 1, (int)filter.get("limit"));
//                Page<Order> ordersPage;
//
//                // Tìm đơn hàng theo userId và status (nếu có)
//                if (status != null && !status.isEmpty()) {
//                    ordersPage = orderRepository.findByUserIdAndStatus(userId, status, pageable);
//                } else {
//                    ordersPage = orderRepository.findByUserId(userId, pageable);
//                }
//
//                // Trả về dữ liệu đơn hàng và thông tin phân trang
//                PaginatedResponse response = new PaginatedResponse();
//                response.setOrders(ordersPage.getContent());
//                response.setPagination(new Pagination(
//                        page,
//                        limit,
//                        ordersPage.getTotalElements(),
//                        ordersPage.getTotalPages()
//                ));
//                return response;
//            }
//        }catch (Exception e){
//            return e.getMessage();
//        }
        return null;
    }

    @Override
    public Object getAllOrders(Map<String, Object> filter){
        try{

            Pageable pageable = PageRequest.of((int)filter.get("page") - 1, (int)filter.get("limit"));

            Date start = filter.get("startDate") != null ? new Date(filter.get("startDate").toString()) : null;
            Date end = filter.get("endDate") != null ? new Date(filter.get("endDate").toString()) : null;

            Page<Order> ordersPage;

            if(filter.get("search") != null && !filter.get("search").toString().equals("")){
                ordersPage = orderRepository.findByStatusAndPaymentStatusAndCreatedAtBetweenAndShippingAddressFullNameLikeOrTrackingNumberLikeOrCouponCodeLike(
                        filter.get("status").toString(),filter.get("paymentStatus").toString(),start,end,"%" + filter.get("search") + "%",pageable
                );
            }else if(!Objects.isNull(start) && !Objects.isNull(end)){
                ordersPage = orderRepository.findByStatusAndPaymentStatusAndCreatedAtBetween(
                        filter.get("Status").toString(),filter.get("paymentStatus").toString(),start,end,pageable
                );
            }else{
                ordersPage = orderRepository.findAll(pageable);
            }

            Map<String,Object> pagination = new HashMap<>();
            pagination.put("page",filter.get("page"));
            pagination.put("limit",filter.get("limit"));
            pagination.put("total",ordersPage.getTotalElements());
            pagination.put("pages",ordersPage.getTotalPages());

            Map<String,Object> result = new HashMap<>();
            result.put("orders",ordersPage.getContent());
            result.put("pagination",pagination);
            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

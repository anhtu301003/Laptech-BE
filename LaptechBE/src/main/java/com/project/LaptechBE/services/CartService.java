package com.project.LaptechBE.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.LaptechBE.enums.StatusEnum;
import com.project.LaptechBE.models.Cart;
import com.project.LaptechBE.models.Product;
import com.project.LaptechBE.models.User;
import com.project.LaptechBE.repositories.CartRepository;
import com.project.LaptechBE.repositories.ProductRepository;
import com.project.LaptechBE.repositories.UserRepository;
import com.project.LaptechBE.services.IServices.ICartService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

    private final CartRepository cartRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final MongoTemplate mongoTemplate;

    @Override
    public Object addToCart(String userId, String productId, Integer quantity) {
        try{
            var product = productRepository.findById(productId.toString());
            if(!product.isPresent()){
                return "Product not found";
            }

            if(product.get().getStock().doubleValue() < quantity){
                return "Only "+ product.get().getStock().doubleValue()+" item available";
            }

            var cart = cartRepository.findByUserIdAndStatus(userId.toString(), String.valueOf(StatusEnum.active));

            System.out.println(cart);

            var user = userRepository.findById(userId.toString());

            if(cart == null){
                cart = Cart.builder()
                        .userId(user.get().getId().toString())
                        .products(new ArrayList<>())
                        .totalPrice(0)
                        .status(StatusEnum.active)
                        .build();
            }

            Cart.ProductItem existingProductItem = cart.getProducts().stream()
                    .filter(item -> item.getProductId().toString().equals(productId))
                    .findFirst()
                    .orElse(null);

            if(existingProductItem != null){
                int newQuantity = existingProductItem.getQuantity().intValue() + quantity;
                if(newQuantity > product.get().getStock().intValue()){
                    return "Cannot add more item. Only "+product.get().getStock().intValue()+" items available";
                }
                existingProductItem.setQuantity(newQuantity);
                existingProductItem.setSubtotal(newQuantity * product.get().getPrice().doubleValue());
            }else{
                Cart.ProductItem productItem = Cart.ProductItem.builder()
                        .productId(product.get().getId().toString())
                        .subtotal(quantity * product.get().getPrice().intValue())
                        .quantity(quantity.intValue())
                        .name(product.get().getName())
                        .price(product.get().getPrice().doubleValue())
                        .build();

                cart.getProducts().add(productItem);
            }

            cartRepository.save(cart);

            return cart;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getActiveCart(String userId) {
        try{

            Query query = new Query();
            query.addCriteria(where("userId").is(userId).and("status").is(StatusEnum.active));

            Cart cart = mongoTemplate.findOne(query, Cart.class);

            Map<String,Object> result = new HashMap<>();
            result.put("userId",cart.getUserId());
            result.put("totalPrice",cart.getTotalPrice());
            result.put("status",cart.getStatus());
            result.put("lastActive",cart.getLastActive());
            result.put("createdAt",cart.getCreatedAt());
            result.put("updatedAt",cart.getUpdatedAt());
            result.put("_id",cart.getId().toString());

            List<Map<String,Object>> products = new ArrayList<>();

            // Nếu tìm thấy Cart, tiến hành lấy thông tin của sản phẩm trong 'products.productId'
            if (cart != null && cart.getProducts() != null) {
                for (Cart.ProductItem productmap : cart.getProducts()) {
                    // Tạo query để lấy sản phẩm từ collection 'products' bằng 'productId'
                    Query productQuery = new Query(Criteria.where("_id").is(productmap.getProductId()));

                    // Truy vấn sản phẩm từ collection 'products'
                    Product product = mongoTemplate.findOne(productQuery, Product.class);

                    if (product != null) {
                        Map<String, Object> productItem = new HashMap<>();
                        productItem.put("_id",product.getId().toString());
                        productItem.put("name",product.getName());
                        productItem.put("price",product.getPrice().doubleValue());
                        productItem.put("stock",product.getStock().doubleValue());
                        productItem.put("images",product.getImages());

                        // Đưa productItem vào một đối tượng Map với key "productId"
                        Map<String, Object> productWrapper = new HashMap<>();
                        productWrapper.put("productId", productItem);

                        productWrapper.put("quantity",productmap.getQuantity().intValue());
                        products.add(productWrapper);
                    }
                }
            }
            result.put("products",products);

            if (cart == null) {
                // Nếu không tìm thấy cart, tạo mới cart và lưu vào database
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    cart = Cart.builder()
                            .userId(userOpt.get().getId().toString())
                            .products(new ArrayList<>())
                            .totalPrice(0)
                            .status(StatusEnum.active)
                            .build();
                    cartRepository.save(cart);
                }
            }

            return result;
        }catch (Exception e){
            System.out.println("Service Error - Get Active Cart:"+ e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object updateCartItem(String userId, String productId, Integer quantity) {
        return null;
    }

    @Override
    public Object removeFromCart(String userId, String productId) {
        return null;
    }

    @Override
    public Object clearCart(String userId) {
        return null;
    }


}
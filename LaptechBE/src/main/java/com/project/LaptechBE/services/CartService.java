package com.project.LaptechBE.services;

import com.project.LaptechBE.enums.StatusEnum;
import com.project.LaptechBE.models.Cart;
import com.project.LaptechBE.models.Product;
import com.project.LaptechBE.models.User;
import com.project.LaptechBE.models.submodels.submodelsCart.CartProductItem;
import com.project.LaptechBE.repositories.CartRepository;
import com.project.LaptechBE.repositories.ProductRepository;
import com.project.LaptechBE.repositories.UserRepository;
import com.project.LaptechBE.services.IServices.ICartService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

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
            var product = productRepository.findById(productId);
            if(product.isEmpty()){
                return "Product not found";
            }
            if(product.get().getStock().doubleValue() < quantity){
                return "Only "+ product.get().getStock().doubleValue()+" item available";
            }
            Query query = new Query();
            query.addCriteria(Criteria.where("userId").is(new ObjectId(userId)).and("status").is(StatusEnum.active));

            // Tìm cart trong database
            Cart cart = mongoTemplate.findOne(query, Cart.class);

            var userOpt = userRepository.findById(userId);
            User user = userOpt.orElseThrow();

            if(Objects.isNull(cart)){
                cart = Cart.builder()
                        .userId(user)
                        .products(new ArrayList<>())
                        .totalPrice(0)
                        .status(StatusEnum.active)
                        .build();
            }
            CartProductItem existingProductItem = cart.getProducts().stream()
                    .filter(item -> Objects.equals(item.getProductId().getId(), new ObjectId(productId)))
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
                CartProductItem productItem = CartProductItem.builder()
                        .productId(product.get())
                        .subtotal(quantity * product.get().getPrice().intValue())
                        .quantity(quantity)
                        .name(product.get().getName())
                        .images(product.get().getImages())
                        .stock(product.get().getStock().intValue())
                        .price(product.get().getPrice().doubleValue())
                        .Specifications(new ArrayList<>())
                        .build();
                cart.getProducts().add(productItem);
            }

            cart.calculateTotalPrice();

            cartRepository.save(cart);

            getActiveCart(userId);
            return cart;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> getActiveCart(String userId) {
        try {
            // Tạo query để tìm cart của user có trạng thái active
            Query query = new Query();
            query.addCriteria(Criteria.where("userId").is(new ObjectId(userId)).and("status").is(StatusEnum.active));

            // Tìm cart trong database
            Cart cart = mongoTemplate.findOne(query, Cart.class);

            // Nếu không tìm thấy cart, tạo mới
            if (Objects.isNull(cart)) {
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    cart = Cart.builder()
                            .userId(userOpt.get())
                            .products(new ArrayList<>())
                            .totalPrice(0)
                            .status(StatusEnum.active)
                            .build();
                    cartRepository.save(cart);
                } else {
                    throw new RuntimeException("User not found with ID: " + userId);
                }
            }

            // Chuẩn bị kết quả trả về
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> products = new ArrayList<>();

            // Nếu cart có danh sách products, lấy thông tin sản phẩm từ collection 'products'
            if (Objects.nonNull(cart.getProducts())) {
                for (CartProductItem productItem : cart.getProducts()) {
                    // Tìm thông tin sản phẩm từ Product collection
                    if (productItem != null) {
                        Map<String, Object> productData = new HashMap<>();
                        productData.put("_id", productItem.getProductId().getId().toString());
                        productData.put("name", productItem.getName());
                        productData.put("price", productItem.getPrice().doubleValue());
                        productData.put("stock", productItem.getStock().doubleValue());
                        productData.put("images", productItem.getImages() == null ? new ArrayList<>() : productItem.getImages());

                        // Gói dữ liệu sản phẩm kèm theo số lượng
                        Map<String, Object> productWrapper = new HashMap<>();
                        productWrapper.put("productId", productData);
                        productWrapper.put("quantity", productItem.getQuantity().intValue());

                        products.add(productWrapper);
                    }
                }
            }

            // Tính toán tổng giá trị của giỏ hàng
            cart.calculateTotalPrice();

            // Bổ sung thông tin cart vào kết quả trả về
            result.put("_id", cart.getId().toString());
            result.put("userId", cart.getUserId());
            result.put("totalPrice", cart.getTotalPrice().doubleValue());
            result.put("status", cart.getStatus());
            result.put("lastActive", cart.getLastActive());
            result.put("createdAt", cart.getCreatedAt());
            result.put("updatedAt", cart.getUpdatedAt());
            result.put("products", products);

            return result;

        } catch (Exception e) {
            System.out.println("Service Error - Get Active Cart: " + e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object updateCartItem(String userId, String productId, Integer quantity) {
        try{
            var product = productRepository.findById(productId.toString());
            if(!product.isPresent()){
                return "Product not found";
            }

            if(product.get().getStock().doubleValue() < quantity){
                return "Only "+ product.get().getStock().doubleValue()+" item available";
            }

            var cart = cartRepository.findByUserIdAndStatus(new ObjectId(userId), String.valueOf(StatusEnum.active));

            if(cart == null){
                return "Cart not found";
            }

            int productIndex = IntStream.range(0, cart.getProducts().size())
                    .filter(i -> cart.getProducts().get(i).getProductId().toString().equals(productId))
                    .findFirst()
                    .orElse(-1);

            if(productIndex == -1){
                return "Product not found in cart";
            }

            cart.getProducts().get(productIndex).setQuantity(quantity);
            cart.getProducts().get(productIndex).setSubtotal(quantity * product.get().getPrice().doubleValue());
            cart.getProducts().get(productIndex).setPrice(product.get().getPrice().doubleValue());
            cartRepository.save(cart);
            return cart;
        } catch (Exception e) {
            System.out.println("Service Error - Update Cart Item: " + e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object removeFromCart(String userId, String productId) {
        try{
            Query query = new Query();
            query.addCriteria(Criteria.where("userId").is(new ObjectId(userId)).and("status").is(StatusEnum.active));

            // Tìm cart trong database
            Cart cart = mongoTemplate.findOne(query, Cart.class);

            if(Objects.isNull(cart)){
                return "Cart not found";
            }

            CartProductItem productToRemove = null;

            for(CartProductItem productItem : cart.getProducts()){
                if(productItem.getProductId().getId().toString().equals(productId)){
                    productToRemove = productItem;
                    break;
                }
            }

            if(productToRemove == null){
                throw new RuntimeException("Product not found in cart");
            }

            cart.getProducts().remove(productToRemove);
            cart.calculateTotalPrice();

            cart = cartRepository.save(cart);

//            cart.calculateTotalPrice(); // A method that recalculates totalPrice

            // Save the updated cart
            return cart;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object clearCart(String userId) {
        try{
            var cart = cartRepository.findByUserIdAndStatus(new ObjectId(userId), String.valueOf(StatusEnum.active));
            if(Objects.isNull(cart)){
                return "Cart not found";
            }

            cart.setProducts(new ArrayList<>());
            cart.setTotalPrice(0);
            cartRepository.save(cart);
            return true;

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


}
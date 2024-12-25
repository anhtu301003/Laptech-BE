package com.project.LaptechBE.services.IServices;

import com.project.LaptechBE.models.Cart;
import org.bson.types.ObjectId;

public interface ICartService {

    public Object addToCart(String userId, String productId, Integer quantity);

    public Object getActiveCart(String userId);

    public Object updateCartItem(String userId, String productId, Integer quantity);

    public Object removeFromCart(String userId, String productId);

    public Object clearCart(String userId);
}

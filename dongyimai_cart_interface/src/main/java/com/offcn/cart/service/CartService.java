package com.offcn.cart.service;

import com.offcn.group.Cart;

import java.util.List;

public interface CartService {

    public List<Cart>  addGoodsToCartList(List<Cart> srcCartList , Long itemId, Integer num);

    /** 将购物车的数据存储到redis */
    public void saveCartListToRedis(String username,List<Cart> cartList);

    /** 将redis中的数据取出 根据用户名  */
    public List<Cart> findCartListFromRedis(String username);

    /** 将cookie的数据合并到redis中 */
    public List<Cart> AddCartToRedisList(List<Cart> cookieCarts,List<Cart> redisCarts);

}

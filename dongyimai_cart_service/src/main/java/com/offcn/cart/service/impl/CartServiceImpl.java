package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.group.Cart;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("redis中存入购物车数据");
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中获取数据");
        List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps("cartList").get(username);
        if(cartList == null){
            cartList = new ArrayList();
        }
        return cartList;
    }

    @Override
    public List<Cart> AddCartToRedisList(List<Cart> cookieCarts, List<Cart> redisCarts) {
        System.out.println("合并购物车");
        for(Cart cart : cookieCarts){
            for(TbOrderItem orderItem : cart.getOrderItemList()){
                // 将cookie中的数据依次那次存储到redis中
                redisCarts = addGoodsToCartList(redisCarts, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return redisCarts;
    }

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> srcCartList, Long itemId, Integer num) {
        // 1.根据itemId获取数据
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        // 判断有效性
        if(tbItem == null){
            throw new RuntimeException("商品不存在");
        }
        if(!tbItem.getStatus().equals("1")){
            throw new RuntimeException("商品非法");
        }
//        if(num <= 0){
//            throw new RuntimeException("数量错误");
//        }
        // 2.获取商家ID
        String sellerId = tbItem.getSellerId();
        // 3. 根据商家的ID判断购物车中是否存在该商家
        Cart cart = searchCartBySellerId(srcCartList, sellerId);
        if(cart == null){ // 没有对应的商家
            // 4 创建新的购物车
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());
            TbOrderItem orderItem = createOrderItem(tbItem, num);
            List orderItemList = new ArrayList<>();
            orderItemList.add(orderItem);
            // 将商品明细添加到购物车中
            cart.setOrderItemList(orderItemList);
            // 将新增的cart加入到CartList中
            srcCartList.add(cart);

        }else{ // 有对应的商家
            // 5 判断购物车明细中是否有要被添加的商品
            TbOrderItem tbOrderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if(tbOrderItem == null){ // 该商家的购物列表中没有该商品
                tbOrderItem = createOrderItem(tbItem, num);
                cart.getOrderItemList().add(tbOrderItem);
            }else{ // 该商家的购物列表中有该商品
                // 需要修改数量
                tbOrderItem.setNum(tbOrderItem.getNum() + num);
                // 修改商品的小计
                tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getNum() * tbOrderItem.getPrice().doubleValue()));
                // 如果商品明细中数量为0 则该商品从商品列表中删除
                if(tbOrderItem.getNum() <= 0){
                    cart.getOrderItemList().remove(tbOrderItem);
                }
                // 如果商家中的所有商品都没有，移除商家
                if(cart.getOrderItemList().size() == 0){
                    srcCartList.remove(cart);
                }

            }

        }
        //System.out.println(srcCartList.size());
        return srcCartList;
    }
    // 根据商家ID 查询购物车对象
    private Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        for(Cart cart : cartList){
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
    // 根据商品id判断是否有对应的商品
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItems,Long itemId){
        for(TbOrderItem orderItem : orderItems){
            if(orderItem.getItemId().longValue() == itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }
    // 创建订单明细
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        TbOrderItem tbOrderItem = new TbOrderItem();
        tbOrderItem.setGoodsId(item.getGoodsId());
        tbOrderItem.setItemId(item.getId());
        tbOrderItem.setNum(num);
        tbOrderItem.setPicPath(item.getImage());
        tbOrderItem.setSellerId(item.getSellerId());
        tbOrderItem.setTitle(item.getTitle());
        tbOrderItem.setPrice(item.getPrice());
        tbOrderItem.setTotalFee(new BigDecimal(item.getPrice() .doubleValue()* num));
        return tbOrderItem;
    }
}

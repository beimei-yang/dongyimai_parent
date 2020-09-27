package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.common.utils.CookieUtil;
import com.offcn.entity.Result;
import com.offcn.group.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;



    /**
     * @Author dsy
     * @Date 2020/9/18 14:08
     * @Description 获取cookie中的数据
     * @Param [request]
     * @Return java.util.List<com.offcn.group.Cart>
     * @Since version-1.0
     */

    @RequestMapping("/findAllCartList")
    public List<Cart> findAllCartList(HttpServletRequest request,HttpServletResponse response){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 获取cookie中原有的数据
        String cartListCookie = CookieUtil.getCookieValue(request, "cartList", "UTF-8");

        // 当前cookie中没有内容
        if(cartListCookie == null || cartListCookie.equals("")){
            cartListCookie = "[]";
        }
        // cookie中的数据
        List<Cart> cartList_cookie = JSON.parseArray(cartListCookie, Cart.class);

        // 没有登录的情况
        if(username == null || username == "" || username.equals("anonymousUser") ){ // 存储在cookie中
            return cartList_cookie;
        }else{ // 已经登录 存储在redis中
            // 合并cookie中的数据
            // 获取redis中的数据
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            // 合并
            cartList_redis = cartService.AddCartToRedisList(cartList_cookie, cartList_redis);
            // 写入到redis中
            cartService.saveCartListToRedis(username,cartList_redis);
            // 清空cookie
            CookieUtil.deleteCookie(request,response,"cartList");
            return cartList_redis;
        }

    }

    @CrossOrigin(origins = "http://localhost:9005")
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodstoCartLis(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response){

//        response.setHeader("Access-Control-Allow-Origin","http://localhost:9005");
//        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录的是:" + username);

        // 获取之前的数据
        List<Cart> allCartList = findAllCartList(request,response);
        // 调用service方法
        allCartList = cartService.addGoodsToCartList(allCartList,itemId,num);

        if(username == null || username == "" || username.equals("anonymousUser")){
            // cookie中的操作
            try {
                // 将购物车数据放入到 cookie中
                String s = JSON.toJSONString(allCartList);
                CookieUtil.setCookie(request,response,"cartList",s,(60*60*24*10),"UTF-8");
                return new Result(true,"添加cookie购物车成功");
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false,"添加购物车失败");
            }

        }else{ // redis中的操作
            try {
                cartService.saveCartListToRedis(username,allCartList);
                return new Result(true,"添加购物车到redis成功");
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false,"添加购物车到redis失败");
            }

        }


    }

    @RequestMapping("/getUserName")
    public Map getUserName(){
        Map map = new HashMap();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",name);
        return map;
    }

}

app.service('cartService',function($http){

    // 保存订单
    this.addOrder = function(order){
        return $http.post("order/add.do?",order);
    }

    // 获取购物车列表
    this.findAllCartList = function(){
        return $http.get("cart/findAllCartList.do");
    }

    // 添加购物车
    this.addGoodsToCartList = function(itemId, num){
        return $http.get("cart/addGoodsToCartList.do?itemId=" + itemId + "&num=" + num);
    }

    // 获取用户名
    this.getUserName = function(){
        return $http.get("cart/getUserName.do");
    }

    // 求总费用
    this.getSum = function(cartList){
        var totalValue = {totalNum : 0,totalMoney : 0.00};
        for(var i =0; i < cartList.length; i++){
            var cart = cartList[i];
            for(var j = 0;j < cart.orderItemList.length; j++){
                var orderItem = cart.orderItemList[j];
                totalValue.totalNum += orderItem.num;
                totalValue.totalMoney += orderItem.totalFee;
            }
        }
        return totalValue;
    }


})
app.controller("cartController",function($scope,cartService,addressService){

    $scope.findAllCartList = function(){
        cartService.findAllCartList().success(
            function(response){
                $scope.cartList = response;
                $scope.totalValue = cartService.getSum($scope.cartList);
            }
        )
    }

    $scope.addGoodsToCartList= function(itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(
           function(response){
               if(response.success){
                   $scope.findAllCartList(); // 刷新列表的数据
               }else{
                   alert(response.message); // 弹出错误提示
               }
           }
        )
    }

    $scope.getUserName = function(){
        cartService.getUserName().success(
            function(response){
                $scope.username = response.username;
            }
        )
    }

    $scope.findAddressList = function(){
        addressService.findAddressList().success(
            function(response){
                $scope.addresssList = response;
                // 设置默认地址
                for(var i = 0; i < $scope.addresssList.length; i++){
                    if($scope.addresssList[i].isDefault == "1"){
                        $scope.address = $scope.addresssList[i];
                    }
                }
            }
        )
    }
    // 选中的地址是
    //$scope.address = "";

    // 选择地址
    $scope.selectAddress = function(address){
        $scope.address = address;
    }

    // 添加被选中的样式
    $scope.isSelectedAddress = function(address){
        if(address == $scope.address){
            return true;
        }else{
            return false;
        }
    }

    // 选择支付方式
    $scope.order = {paymentType:"1"}

    $scope.selectPayType = function(type){
        $scope.order.paymentType = type;
    }

    $scope.isSelectType = function(type){
        if($scope.order.paymentType == type){
            return true;
        }else{
            return false;
        }
    }

    // 保存订单
    $scope.addOrder = function(){
        $scope.order.receiver = $scope.address.contact;
        $scope.order.receiverAreaName = $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        cartService.addOrder($scope.order).success(
            function(response){
                if(response.success){
                    // 支付宝支付
                    if($scope.order.paymentType == "1"){
                        location.href="pay.html";
                    }else{ // 货到付款
                        location.href="paysuccess.html";
                    }
                }
            }
        )
    }




})
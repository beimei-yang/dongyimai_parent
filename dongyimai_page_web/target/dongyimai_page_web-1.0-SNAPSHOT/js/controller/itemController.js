app.controller("itemController",function($scope,$http){
    $scope.addNum = function(x){
        $scope.num = $scope.num + x;
        console.log($scope.num);
        if($scope.num < 1){
            $scope.num = 1;

        }
    }

    $scope.loadItem = function(){
        // 获取第一款商品(当前显示的内容)
        $scope.selectItem = skuList[0];
        // 当前页面中被选择的规格
        // 硬克隆
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.selectItem.spec));

    }

    $scope.specificationItems = {} // 用户选择的规格

    // 将用户选择的规格加入到对象中
    $scope.selectSpecificationItems = function(name,value){
        $scope.specificationItems[name] = value;
        //console.log("从数据库中读取的数据：" + $scope.selectItem.spec.网络);
        matchSku();
    }

    $scope.isSelected = function(name,value){
        if($scope.specificationItems[name] == value){
            return true;
        }else{
            return false;
        }
    }

    // 判断两个对象是否相同（属性值和属性）
    matchObject = function(obj1,obj2){
        for(var key in obj1){
            if(obj1[key] != obj2[key]){
                return false;
            }
        }
        return true;
    }

    // 将数据库中获取的 skuList的对象和用户选择的对象 进行判断是否相同
    matchSku = function(){
        for(var i = 0; i < skuList.length; i++){
            if(matchObject(skuList[i].spec,$scope.specificationItems)){
                $scope.selectItem = skuList[i];
                return ;
            }
        }
        // 表示一个都没有匹配上
        $scope.selectItem = {id:0,title:"该商品已售罄",price:0};
    }

    $scope.addCart = function(){
        //alert("itemId:" + $scope.selectItem.id + ",num:" + $scope.num);
        $http.get("http://localhost:9007/cart/addGoodsToCartList.do?itemId=" + $scope.selectItem.id + "&num=" + $scope.num,{'withCredentials':true}).success(
            function(response){
                if(response.success){
                    location.href="http://localhost:9007/cart.html";
                }else{
                    alert(response.message);
                }
            }
        )
    }

})
app.controller("brandController",function($scope,$controller,brandService){

    // 继承baseController.js中的内容
    $controller("baseController",{$scope:$scope});

    $scope.findAll = function(){
        brandService.findAll().success(
            function(response){
                $scope.list = response;
            }
        )
    }

    $scope.findByPage = function(pageNum,pageSize){
        brandService.findByPage(pageNum,pageSize).success(
            function(response){
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    }

    $scope.save = function(){
        if($scope.entity.id != null){
            // 修改
            //$http.post("../brand/update.do",$scope.entity)
            brandService.update($scope.entity).success(
                function(response){
                    if(response.success){
                        // 提交成功
                        $scope.reloadList();
                    }else{
                        alert(response.message);
                    }
                }
            )
        }else{
            // 添加
            //$http.post("../brand/add.do",$scope.entity)
            brandService.add($scope.entity).success(
                function(response){
                    if(response.success){
                        // 提交成功
                        $scope.reloadList();
                    }else{
                        alert(response.message);
                    }
                }
            )
        }

    }

    $scope.findById = function(id){
        //$http.get("../brand/findById.do?id=" + id)
        brandService.findById(id).success(
            function(response){
                $scope.entity = response;
            }
        )
    }

    $scope.delete = function(){
        //$http.get("../brand/delete.do?ids=" +  $scope.selectIds)
        brandService.delete($scope.selectIds).success(
            function(response){
                if(response.success){
                    // 提交成功
                    $scope.reloadList();
                    $scope.selectIds = [];
                }else{
                    alert(response.message);
                }
            }
        )
    }

    $scope.search = function(pageNum,pageSize){
        //$http.post("../brand/search.do?pageNum="+pageNum+"&pageSize="+pageSize,$scope.searchEntity)
        brandService.search(pageNum,pageSize,$scope.searchEntity).success(
            function(response){
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    }







})
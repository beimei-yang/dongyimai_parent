app.controller("baseController",function($scope){


    $scope.searchEntity={};//定义搜索对象

    $scope.list = [];

    // 将来进行查询的对象
    $scope.searchEntity = {};

    //定义一个数组用来存放要被删除的id
    $scope.selectIds = [];


    $scope.reloadList = function(){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)
        $scope.selectIds = [];

    }

    $scope.flag = function (){
        return false;
    }

    // 分页控件
    $scope.paginationConf = {
        currentPage: 1, // 当前页
        totalItems: 10, // 总的记录数
        itemsPerPage: 10, // 每一页显示的记录数
        perPageOptions: [10, 20, 30, 40, 50], // 用户可选的每页显示的记录数
        onChange: function () { // 以上所有的属性一旦发生修改则调用该方法
            $scope.reloadList();//重新加载
        }
    }



  

    $scope.selectDelete = function(id,$event){
        // 被选中，表示该id加入到被删除的selectIds中
        if($event.target.checked){
            // 将id添加到 selectIds 中
            $scope.selectIds.push(id);

        }else{
            // 从selectIds中被删除的id
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1);
        }
    }

    // 全选
    $scope.selectAll = function($event){
        if($event.target.checked){
            $scope.selectIds = [];
            // 全选状态
            for(var i = 0; i < $scope.list.length; i++){
                $scope.selectIds.push($scope.list[i].id);
            }
        }else{
            // 全不选状态
            $scope.selectIds = [];
        }
    }

    // 查看当前选项的状态
    $scope.isSelected = function(id){

        if($scope.selectIds.indexOf(id) >= 0){
            return true;
        }else{
            return false;
        }

    }

    $scope.isAllSelected = function() {
        if ($scope.selectIds.length == $scope.list.length) {
            return true;
        } else {
            return false;
        }
    }



  


})
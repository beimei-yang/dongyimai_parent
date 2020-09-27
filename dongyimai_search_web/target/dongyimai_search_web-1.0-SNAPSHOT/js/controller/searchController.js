app.filter("trustHTML",['$sce',function($sce){ // $sce提供了一种将可能存在风险的内容标记为可信任的内容
    return function(data){
        // 将data 转为html可以解析的内容
        return $sce.trustAsHtml(data);
    }
}])

app.controller("searchController",function($scope,$location,searchService){

    $scope.searchMap={'keywords':'','category':"",'brand':"",'spec':{},'price':"","pageNo":1,'pageSize':20,'sortField':'','sort':''}

    $scope.addKeywords = function(){
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }

    $scope.search = function(){

        searchService.search($scope.searchMap).success(
            function(response){
                $scope.resultMap = response;
                buildPage();

            }
        )
    }

    $scope.queryByPage = function(pageNo){
        if(pageNo < 1 || $scope.searchMap.pageNo < 1){
            $scope.searchMap.pageNo = 1; // 最小值越界
        }else if(pageNo > $scope.resultMap.totalPages || $scope.searchMap.pageNo > $scope.resultMap.totalPages){
            $scope.searchMap.pageNo = $scope.resultMap.totalPages; // 最大值越界
        }else{
            $scope.searchMap.pageNo = parseInt(pageNo);
        }
        $scope.search();
    }

    buildPage = function(){
        $scope.pages = []; // 存放页码
        var minPage = 1;
        var maxPage = $scope.resultMap.totalPages;

        $scope.firstDot = true;
        $scope.lastDot = true;

        //console.log($scope.resultMap.totalPages);
        if($scope.resultMap.totalPages > 5){
            if($scope.searchMap.pageNo <= 3) {
                // $scope.page=[1,2,3,4,5...];
                maxPage = 5;
                $scope.firstDot = false; // 前面没点
            }else if($scope.searchMap.pageNo >= $scope.resultMap.totalPages - 2){
                // $scope.page=[...最后5页]
                minPage = $scope.resultMap.totalPages - 4;
                $scope.lastDot = false;  // 后面没点
            }else{
                minPage = $scope.searchMap.pageNo - 2;
                maxPage = $scope.searchMap.pageNo + 2;
            }
        }else{ // 总页数小于5
            $scope.firstDot = false;
            $scope.lastDot = false;
        }
        for(var i = minPage; i <= maxPage; i++){
            $scope.pages.push(i);
        }

    }

    $scope.addSearchItem = function(key,value){
        if(key == 'category' || key == "brand" || key == "keywords" || key=="price"){
            $scope.searchMap[key]=value;
        }else{ //规格
            $scope.searchMap.spec[key] = value;
            //$scope.searchMap.spec["网络"] = "3G" 点击 网络 3G的时候
        }
        if($scope.searchMap['keywords'] == ""){
            $scope.resultMap={rows:[]};
            alert("请输入关键词");
            return ; //结束方法执行
        }
        $scope.searchMap.pageNo = 1;
        $scope.search();
    }

    $scope.removeSearchItem = function(key){
        if(key == 'category' || key == "brand" || key == "keywords" || key=="price"){
            $scope.searchMap[key] = "";
        }else{
            delete $scope.searchMap.spec[key];
        }
        if($scope.searchMap['keywords'] == ""){
            $scope.resultMap={rows:[]};
            alert("请输入关键词");
            return ; //结束方法执行
        }
        $scope.searchMap.pageNo = 1;
        $scope.search();
    }

    $scope.keyboardSearch = function($event){
       // alert($event.keyCode);
        if($event.keyCode==13) {
           $scope.search();
            $scope.searchMap.pageNo = 1;
        }

    }

    $scope.isPage = function(p){
        if(parseInt(p) == parseInt($scope.searchMap.pageNo)){
            return true;
        }else{
            return false;
        }
    }

    // 排序
    $scope.sortSearch = function(sortField,sort){
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    }

    // 隐藏品牌
    $scope.keywordsContentsBrand = function(){
        for(var i = 0; i < $scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0){
                return true;
            }
        }
        return false;
    }
})
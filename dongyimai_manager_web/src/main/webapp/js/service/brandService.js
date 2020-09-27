// 专门用来处理请求的
app.service("brandService",function($http){

    this.findAll = function(){
        return $http.get("../brand/findAll.do");
    }

    this.findByPage = function(pageNum,pageSize){
        return $http.get("../brand/findByPage.do?pageNum="+pageNum+"&pageSize="+pageSize)
    }

    this.update = function(entity){
        return $http.post("../brand/update.do",entity);
    }

    this.add = function(entity){
        return $http.post("../brand/add.do",entity)
    }

    this.findById = function(id){
        return $http.get("../brand/findById.do?id=" + id);
    }

    this.delete = function(ids){
        return $http.get("../brand/delete.do?ids=" +  ids);
    }
    // $http.post("../brand/search.do?pageNum="+pageNum+"&pageSize="+pageSize,$scope.searchEntity)
    this.search = function(pageNum,pageSize,entity){
        return $http.post("../brand/search.do?pageNum="+pageNum+"&pageSize="+pageSize,entity);

    }

    this.selectBrandList = function(){
        return $http.get("../brand/selectBrandList.do");
    }
})
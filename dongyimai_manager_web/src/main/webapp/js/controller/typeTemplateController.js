 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller   ,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承

	$scope.entity={customAttributeItems:[]};

	$scope.brands = {"data":[]};

	$scope.specs = {"data":[]};

	// 定义一个品牌的模拟数据
	//$scope.brands = {"data":[{"id":1,"text":"肯德基"},{"id":2,"text":"麦当劳"},{"id":3,"text":"德克士"}]};

	$scope.findBrandList = function(){
		brandService.selectBrandList().success(
			function(response){
				$scope.brands = {"data":response};
			}
		)
	}

	$scope.findSpecList = function(){
		specificationService.selectSpecList().success(
			function(response){
				$scope.specs = {"data":response};
			}
		)
	}

	$scope.addRow = function(){
		$scope.entity.customAttributeItems.push({});
	}

	$scope.delRow = function(index){
		$scope.entity.customAttributeItems.splice(index,1);
	}

	$scope.init = function(){
		$scope.findBrandList();
		$scope.findSpecList();
	}


    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//alert($scope.entity);
				$scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
				$scope.entity.specIds = JSON.parse($scope.entity.specIds);
				$scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	// 定义一个方法获取json数组中每个元素的text的值
	$scope.jsonToString = function(jsonString){
		var json = JSON.parse(jsonString);
		var value = "";
		for(var i = 0; i < json.length; i++){
			if(i > 0){
				value += "，";
			}
			value = value + json[i]["text"] ;
		}
		return value;
	}
    
});	
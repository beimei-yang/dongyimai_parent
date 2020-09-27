 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承

	// 表单提交的对象
	//$scope.entity.tbGoodsDesc.specificationItems
	$scope.entity={tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[]},itemList:[]};

	$scope.entity.tbGoods.typeTemplateId = 0;

	$scope.image_entity = {};

	// 定义一个用来解析status的数组
	$scope.status = ['未审核','已审核','审核未通过','关闭'];

	// 存放所有的分类信息
	$scope.itemCatList = [];


	$scope.findAllItemCatList = function(){
		itemCatService.findAll().success(
			function(response){
				for(var i = 0 ; i < response.length;i++){
					$scope.itemCatList[response[i].id] = response[i].name;
				}
				//console.log($scope.itemCatList[2]);
			}

		)
	}


    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		// id是通过列表页传递过来的 (接收参数)
		var id = $location.search()['id'];
		if(id == null){
			return ;  //停止该方法的执行
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				editor.html($scope.entity.tbGoodsDesc.introduction);
				// 解析图片
				$scope.entity.tbGoodsDesc.itemImages = JSON.parse($scope.entity.tbGoodsDesc.itemImages);
				// 解析扩展属性
				if($scope.entity.tbGoodsDesc.customAttributeItems != null) {
					$scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);
				}
				// 规格
				$scope.entity.tbGoodsDesc.specificationItems = JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
				// SKU数据
				//alert($scope.entity.itemList.length);
				for(var i = 0;i < $scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
				}
				console.log($scope.entity.itemList);
			}
		);				
	}

	// 定义一个方法用来识别被选中的规格
	$scope.checkAttributeValue = function(specName,optionName){
		var items = $scope.entity.tbGoodsDesc.specificationItems;
		var object = $scope.searchObjectByKey(items,"attributeName",specName)
		if(object == null){
			return false;
		}else{ //表示该规格有规格项被选中
			if(object.attributeValue.indexOf(optionName) >= 0){ //表示该规格项是被选中的
				return true;
			}
			return false;

		}

	}
	
	// 添加
	$scope.add = function(){
		// 将富文本编辑器的内容赋值给 属性introduciton
		$scope.entity.tbGoodsDesc.introduction = editor.html();
		var service ;
		if($scope.entity.tbGoods.id == null){
			goodsService.add($scope.entity).success(
				function(response){
					if(response.success){
						alert(response.message);
						$scope.entity={tbGoods:{},tbGoodsDesc:{},itemList:[]};
						// 清空富文本编辑器的内容
						editor.html("");
						// 跳转到 goods.html中
						location.href="goods.html";
					}else{
						alert(response.message);
					}
				}
			)
		}else{
			goodsService.update($scope.entity).success(
				function(response){
					if(response.success){
						alert(response.message);
						$scope.entity={tbGoods:{},tbGoodsDesc:{},itemList:[]};
						// 清空富文本编辑器的内容
						editor.html("");
					}else{
						alert(response.message);
					}
				}
			)
		}
	}


	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
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
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	// 文件上传
	$scope.uploadFile = function(){
		uploadService.upload().success(
			function(response){
				if(response.success){
					// 接收文件上传的路径
					$scope.image_entity.url = response.message;
				}else{
					alert(response.message);
				}
			}
		).error(function(){
			alert("上传程序崩溃");
		})
	}

	$scope.add_image = function(){
		$scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity);
	}

	$scope.delImage = function(index){
		$scope.entity.tbGoodsDesc.itemImages.splice(index,1);
	}

	// 获取一级分类
	$scope.select1ItemCartList = function(){
		itemCatService.findByParentId(0).success(
			function(response){
				$scope.itemCat1List = response;

			}
		)
	}

	$scope.$watch("entity.tbGoods.category1Id",function(newValue,oldValue){
		if(newValue) {
			//alert(newValue);
			itemCatService.findByParentId(newValue).success(
				function(response){
					$scope.itemCat2List = response;
					$scope.itemCat3List = [];
					$scope.entity.tbGoods.typeTemplateId = 0;
				}
			)
		}
	})

	$scope.$watch("entity.tbGoods.category2Id",function(newValue,oldValue){
		if(newValue) {
			//alert(newValue);
			itemCatService.findByParentId(newValue).success(
				function(response){
					$scope.itemCat3List = response;

				}
			)
		}
	})

	$scope.$watch("entity.tbGoods.category3Id",function(newValue,oldValue){
		if(newValue) {
			//alert(newValue);
			itemCatService.findOne(newValue).success(
				function(response){
					$scope.entity.tbGoods.typeTemplateId = response.typeId;

				}
			)
		}
	})

	// 根据模板ID 获取对应的品牌列表
	$scope.$watch("entity.tbGoods.typeTemplateId",function(newValue,oldValue){
		if(newValue != 0 && newValue!=undefined){
			typeTemplateService.findOne(newValue).success(
				function(response){
					$scope.typeTemplate = response;
					// 品牌
					$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
					if($location.search()['id'] == null) {
						// 扩展属性
						$scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
					}
				}
			)

			typeTemplateService.findSpecList(newValue).success(
				function(response){
					$scope.specList = response;
				}
			)
		}
	})

	// 定义一个方法，用来判断list中是否有指定的键值对
	// list 要被查询的集合
	// key 是查询的键值对中的键
	// value 是查询的键值对中的值
	$scope.searchObjectByKey = function(list,key,keyValue){
		for(var i = 0; i < list.length; i++){
			if(list[i][key] == keyValue){
				return list[i]; // 返回对应的键值
			}
		}
		return null;
	}

	// []
	// [{"attributeName:网络，attributeValue:[移动3G,移动3G]}]
	// [{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["5.5寸","5寸"]}]

	$scope.updateSpecAttribute = function($event,name,value){
		var object = $scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems,'attributeName',name);
		if(object == null){ // 第一次添加本属性的内容
			$scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
			console.log($scope.entity.tbGoodsDesc.specificationItems);
		}else{ // 已经有该属性的内容
			if($event.target.checked){ // 选中的状态
				object.attributeValue.push(value);
			}else{ //取消的状态
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);
				if(object.attributeValue.length == 0){
					$scope.entity.tbGoodsDesc.specificationItems.splice($scope.entity.tbGoodsDesc.specificationItems.indexOf(object),1);
				}
			}

		}
		$scope.createItemList();

	}

	// 生成 SKU表的表单
	$scope.createItemList = function(){
		$scope.entity.itemList = [{spec:{},price:0,num:10000,status:"0",isDefault:"0"}];
		var items = $scope.entity.tbGoodsDesc.specificationItems;
		for(var i = 0; i < items.length; i++){
			$scope.entity.itemList = addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue)
		}
	}
	//{spec:{"联通3G",16G},price:0,num:10000,status:"0",isDefault:"0"}

	//添加字段
	addColumn = function(list,columnName,columnValues){
		var newList = [];
		for(var i = 0; i < list.length; i++){
			var oldRow = list[i];
			for(var j = 0; j < columnValues.length;j++){
				var newRow = JSON.parse(JSON.stringify(oldRow));
				newRow.spec[columnName] = columnValues[j];
				newList.push(newRow);
			}
		}

		return newList;
	}
	//
	// $scope.test = function(list,demokey,demoValue){
	// 	list.push({demoKey:demoValue});
	// 	//console(list);
	// 	return list;
	// }
	//
	// $scope.list = [];
	//
	// $scope.testDemo = function() {
	// 	for (var i = 0; i < 10; i++) {
	// 		// 硬克隆
	// 		var newList = JSON.parse(JSON.stringify($scope.list));
	// 		$scope.demo = $scope.test(newList, "key" + i, "value" + i);
	// 		console.log($scope.demo);
	// 	}
	// }




    
});	
app.service("uploadService",function($http){


    this.upload = function() {

        var formData = new FormData();
        formData.append("file", file.files[0]);

        return $http({
            method: "POST",
            url: "../upload.do",
            headers: {"Content-Type": undefined},// multipart/form-data
            data: formData,
            transformRequest: angular.identity  // 默认的提交方式：application/json
        })

    }

})
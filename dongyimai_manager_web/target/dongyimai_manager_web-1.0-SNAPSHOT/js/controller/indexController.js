app.controller("indexController",function($scope,loginService){


    $scope.getUserName = function() {
        loginService.getUserName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        )
    }

})
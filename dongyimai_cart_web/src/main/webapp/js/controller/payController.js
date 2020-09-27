app.controller("payController",function($scope,payService) {

    $scope.payReady = function () {
        payService.createNative().success(
            function (response) {
                $scope.out_trade_no = response.out_trade_no;
                $scope.total_fee = response.total_fee;
                // 生成二维码
                var qr = new QRious({
                    element: document.getElementById("qrious"),
                    size: 300,
                    level: 'H',
                    value: response.qrcode
                })

                $scope.queryPayStatus($scope.out_trade_no);
            }
        )
    }

    $scope.queryPayStatus = function(out_trade_no){
        payService.queryPayStatus(out_trade_no).success(
            function(response){
                if(response.success){
                    location.href="paysuccess.html";
                }else{
                    location.href="payfail.html";
                }
            }
        )
    }


})
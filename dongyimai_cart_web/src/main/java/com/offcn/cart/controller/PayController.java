package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.common.utils.IdWorker;
import com.offcn.entity.Result;
import com.offcn.pay.service.AliPayService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/pay")
@RestController
public class PayController {

    @Reference
    private AliPayService aliPayService;

    @RequestMapping("/createNative")
    public Map createNative(){
        IdWorker idWorker = new IdWorker();
       return  aliPayService.createNative(idWorker.nextId()+"","1");
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){

        while(true){
            Map<String,String> map = aliPayService.queryPayStatus(out_trade_no);
            if(map.get("tradeStatus") != null && map.get("tradeStatus").equals("TRADE_SUCCESS")){
                // 支付成功
                return new Result(true,"支付成功");
            }
            if(map.get("tradeStatus") != null && map.get("tradeStatus").equals("TRADE_CLOSED")){
                return new Result(false,"支付超时");
            }
            if(map.get("tradeStatus") != null && map.get("tradeStatus").equals("TRADE_FINISHED")){
                return new Result(true,"交易结束");
            }

            // 每次查询等待5秒
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

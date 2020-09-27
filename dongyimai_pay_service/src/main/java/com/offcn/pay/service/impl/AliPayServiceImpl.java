package com.offcn.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.offcn.pay.service.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class AliPayServiceImpl implements AliPayService {

    @Autowired
    private DefaultAlipayClient alipayClient;

    /**
     * @Author dsy
     * @Date 2020/9/19 16:18
     * @Description This is description of method
     * @Param [out_trade_no, total_fee 表示分为单位]
     * @Return java.util.Map
     * @Since version-1.0
     */

    @Override
    public Map createNative(String out_trade_no, String total_fee)  {
        Map<String,String> map = new HashMap<>();

        // total_fee 换算为 元
        long total = Long.parseLong(total_fee);
        BigDecimal bigTotal = new BigDecimal(total);
        BigDecimal yuan = new BigDecimal(100L);
        BigDecimal bigYuan = bigTotal.divide(yuan);
        System.out.println("支付金额是:" + bigYuan.doubleValue());

        AlipayTradePrecreateRequest request   =   new   AlipayTradePrecreateRequest (); //创建API对应的request类
        request . setBizContent ( "{"   +
                "    \"out_trade_no\":\"" +out_trade_no+ "\","   + //商户订单号
                "    \"total_amount\":\""+ bigYuan.doubleValue() +"\","   +
                "    \"subject\":\"Iphone6 16G\","   +
                "    \"store_id\":\"NJ_001\","   +
                "    \"timeout_express\":\"90m\"}" ); //订单允许的最晚付款时间
        AlipayTradePrecreateResponse response   = null;

        try {
            response = alipayClient . execute ( request );
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        System . out . print ( "支付结果:" + response . getBody ());
        String code = response.getCode();
        if(code.equals("10000")){ // 支付成功
            // 将订单号返回给map
            map.put("out_trade_no",response.getOutTradeNo());
            // 二维码的链接
            map.put("qrcode",response.getQrCode());
            // 支付金额
            map.put("total_fee",total_fee);
        }


        return map;

    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map map = new HashMap();

        AlipayTradeQueryRequest request   =   new   AlipayTradeQueryRequest (); //创建API对应的request类
        request . setBizContent ( "{"   +
                "    \"out_trade_no\":\"" +out_trade_no+ "\"}" );  //设置业务参数
        AlipayTradeQueryResponse response   = null; //通过alipayClient调用API，获得对应的response类
        try {
            response = alipayClient . execute ( request );
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        System . out . print ( "查询结果:" + response . getBody ());

        map.put("tradeStatus",response.getTradeStatus());
        map.put("out_trade_no",out_trade_no);


        return map;
    }
}

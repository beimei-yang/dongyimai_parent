package com.offcn.pay.service;

import java.util.Map;

public interface AliPayService {


    // 生成支付二维码
    public Map createNative(String out_trade_no,String total_fee);

    // 查询交易结果
    public Map queryPayStatus(String out_trade_no);
}

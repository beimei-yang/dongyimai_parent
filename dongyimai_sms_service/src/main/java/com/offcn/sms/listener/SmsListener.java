package com.offcn.sms.listener;

import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import com.offcn.sms.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

public class SmsListener implements MessageListener {

    @Autowired
    private SmsUtil smsUtil;

    @Override
    public void onMessage(Message message) {

        MapMessage map = (MapMessage)message;
        try {
            // 1.获取手机号
            String moblie = map.getString("mobile");
            // 2.验证码
            String code = map.getString("code");
            // 3.发送短信
            CommonResponse response = smsUtil.sendMessage(moblie, code);
            // 4.显示发送短信的结果
            System.out.println(response.getData());
        } catch (JMSException | ClientException e) {
            e.printStackTrace();
        }

    }
}

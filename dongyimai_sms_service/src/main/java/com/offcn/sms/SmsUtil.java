package com.offcn.sms;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsUtil {

    @Value("${AccessKeyId}")
    private String AccessKeyId;

    @Value("${Secret}")
    private String Secret;


    // 发送短信
    public CommonResponse sendMessage(String mobile,String code) throws ClientException {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", AccessKeyId, Secret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "优就业");
        request.putQueryParameter("TemplateCode", "SMS_188631735");
        request.putQueryParameter("TemplateParam", "{\"code\":\"" +code+ "\"}");

        CommonResponse response = client.getCommonResponse(request);
        return response;
    }

}

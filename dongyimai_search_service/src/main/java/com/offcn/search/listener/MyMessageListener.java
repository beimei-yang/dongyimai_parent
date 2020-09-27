package com.offcn.search.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

public class MyMessageListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        String jsonString = null;
        try {
            jsonString = textMessage.getText();
            List<TbItem> list = JSON.parseArray(jsonString,TbItem.class);
            itemSearchService.importList(list);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

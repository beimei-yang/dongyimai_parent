package com.offcn.search.listener;

import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

public class MyMessageDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage)message;
        try {
            Long[] ids = (Long[])objectMessage.getObject();
            System.out.println(Arrays.toString(ids));
            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

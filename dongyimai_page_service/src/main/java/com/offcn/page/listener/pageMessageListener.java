package com.offcn.page.listener;

import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class pageMessageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        try {
            String idString = textMessage.getText();
            Long id = Long.parseLong(idString);
            itemPageService.createHTML(id);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

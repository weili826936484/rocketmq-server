package com.weili.rocketmq_learn.controller;

import com.weili.rocketmq_learn.jms.message.JmsConfig;
import com.weili.rocketmq_learn.jms.message.MessageProducer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/login")
public class loginController {
    @Autowired
    private MessageProducer messageProducer;

    private String producerTopic = JmsConfig.producerTopic;
    @RequestMapping(value = "/sendMsg/{msg}",method = RequestMethod.GET)
    public Object sendMsg(@PathVariable("msg") String msg){
        //生成一条消息
        Message message = new Message(producerTopic,"tags",msg.getBytes());
        //发送至消息队列
        SendResult result = null;
        try {
            result = messageProducer.getProducer().send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            messageProducer.shutdown();
        }
        return result;
    }
}

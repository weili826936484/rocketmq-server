package com.weili.rocketmq_learn.jms.message;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {
    private String producerGroup = JmsConfig.producerGroup;
    private String ip = JmsConfig.namesrvAddr;
    private DefaultMQProducer defaultMQProducer;

    public MessageProducer(){
        defaultMQProducer = new DefaultMQProducer(producerGroup);
        defaultMQProducer.setNamesrvAddr(ip);
        start();
    }

    private void start() {
        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        defaultMQProducer.shutdown();
    }

    public DefaultMQProducer getProducer(){
        return defaultMQProducer;
    }
}

package com.weili.rocketmq_learn.jms.message;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.stereotype.Component;

/**
 * 订单消息生产者
 */
@Component
public class OrderMessageProducer {
    private final String orderProducerGroup = JmsConfig.orderProducerGroup;
    private final String nameSrv = JmsConfig.namesrvAddr;
    private DefaultMQProducer producer;
    public OrderMessageProducer() {
        producer = new DefaultMQProducer(orderProducerGroup);
        producer.setNamesrvAddr(nameSrv);
        start();
        System.out.println("生产者初始化成功");
    }

    private void start() {
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public void  shutdown(){
        producer.shutdown();
    }

    public DefaultMQProducer getProducer(){
        return producer;
    }
}

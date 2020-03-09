package com.weili.rocketmq_learn.jms.message;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageConsumer implements ApplicationRunner {

    public static void listener() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(JmsConfig.consumerGroup);
        consumer.setNamesrvAddr(JmsConfig.namesrvAddr);
        consumer.subscribe(JmsConfig.producerTopic,"*");
        consumer.setMessageModel(MessageModel.CLUSTERING);//集群消费
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                try {
                    MessageExt message = msgs.get(0);
                    message.setDelayTimeLevel(2);
                    System.out.println(message.toString()+ new String(message.getBody()));
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }catch (Exception e){
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        });
        consumer.start();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        listener();
    }
}

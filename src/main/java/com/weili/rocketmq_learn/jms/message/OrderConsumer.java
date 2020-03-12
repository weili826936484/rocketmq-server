package com.weili.rocketmq_learn.jms.message;

import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderConsumer implements ApplicationRunner {

    private static String consumerGroup = JmsConfig.orderConsumerGroup;
    private static String topic = JmsConfig.orderProducerTopic;
    private static String nameSvr = JmsConfig.namesrvAddr;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("实例化消费者");
        DefaultMQPushConsumer orderConsumer = new DefaultMQPushConsumer(consumerGroup);
        orderConsumer.setNamesrvAddr(nameSvr);
        orderConsumer.setMessageModel(MessageModel.CLUSTERING);
        orderConsumer.subscribe(topic,"user||product||discount||logs||message");
        //orderConsumer.subscribe(topic,"*");
        //负载均衡策略
        //orderConsumer.setAllocateMessageQueueStrategy();
        orderConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        orderConsumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                MessageExt me = msgs.get(0);
                String tags = me.getTags();
                if ("user".equals(tags)) {
                    System.out.println("user:"+me.toString()+ new String(me.getBody()));
                } else if ("product".equals(tags)) {
                    System.out.println("product:"+me.toString()+ new String(me.getBody()));
                } else if ("discount".equals(tags)) {
                    System.out.println("discount:"+me.toString()+ new String(me.getBody()));
                } else if ("message".equals(tags)) {
                    System.out.println("message:"+me.toString()+ new String(me.getBody()));
                } else if ("logs".equals(tags)) {
                    System.out.println("logs:"+me.toString()+ new String(me.getBody()));
                } else {
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        orderConsumer.start();
    }
}

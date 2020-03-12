package com.weili.rocketmq_learn.jms.message;

public class JmsConfig {
    public static String namesrvAddr = "101.200.187.135:9876";
    /**
     * message
     */
    public static String producerGroup = "message-topic-producer";
    public static String producerTopic = "message-topic";
    public static String consumerGroup = "message-topic-consumer";

    /**
     * order
     */
    public static String orderProducerGroup = "order-topic-producer";
    public static String orderProducerTopic = "order-topic";
    public static String orderConsumerGroup = "order-topic-consumer";
}

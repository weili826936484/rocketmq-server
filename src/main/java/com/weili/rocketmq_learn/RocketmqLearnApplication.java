package com.weili.rocketmq_learn;

import com.weili.rocketmq_learn.jms.message.MessageConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RocketmqLearnApplication {
    @Autowired
    private static MessageConsumer messageConsumer;
    public static void main(String[] args) {
        SpringApplication.run(RocketmqLearnApplication.class, args);
    }
}

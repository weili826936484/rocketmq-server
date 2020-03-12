package com.weili.rocketmq_learn.controller;

import com.weili.rocketmq_learn.jms.message.JmsConfig;
import com.weili.rocketmq_learn.jms.message.OrderMessageProducer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {
    private Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderMessageProducer orderMessageProducer;

    /**
     * 演示用 get请求
     * @return
     */
    @RequestMapping(value = "/saveOrder/{orderCode}",method = RequestMethod.GET)
    public Object saveOrder(@PathVariable String orderCode){
        try {
            DefaultMQProducer producer = orderMessageProducer.getProducer();
            //重试2次
            producer.setRetryTimesWhenSendFailed(2);
            //查看用户信息
            Message userMessage = new Message(JmsConfig.orderProducerTopic,"user","获取用户信息".getBytes(RemotingHelper.DEFAULT_CHARSET));
            //10秒后才向消费者发送消息
            userMessage.setDelayTimeLevel(3);
            System.out.println("开始发送");
            producer.send(userMessage, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    long id = arg.hashCode() & Integer.MAX_VALUE;
                    long index = id % mqs.size();
                    return mqs.get((int) index);
                }
            },orderCode);
            //查看商品信息 为验证采用定时消费
            Message productMessage = new Message(JmsConfig.orderProducerTopic,"product","获取商品信息".getBytes(RemotingHelper.DEFAULT_CHARSET));
            producer.send(productMessage, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    long id = arg.hashCode() & Integer.MAX_VALUE;
                    long index = id % mqs.size();
                    return mqs.get((int) index);
                }
            },orderCode);
            //查看优惠信息
            Message discountMessage = new Message(JmsConfig.orderProducerTopic,"discount","查看优惠信息".getBytes(RemotingHelper.DEFAULT_CHARSET));
            //"1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
            discountMessage.setDelayTimeLevel(2);
            producer.send(discountMessage, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    long id = arg.hashCode() & Integer.MAX_VALUE;
                    long index = id % mqs.size();
                    return mqs.get((int) index);
                }
            },orderCode);
            //发送短信 异步发送
            Message message = new Message(JmsConfig.orderProducerTopic,"message","发送短信".getBytes(RemotingHelper.DEFAULT_CHARSET));
            producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    long id = arg.hashCode() & Integer.MAX_VALUE;
                    long index = id % mqs.size();
                    return mqs.get((int) index);
                }
            }, orderCode, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println(sendResult.getSendStatus());
                }

                @Override
                public void onException(Throwable e) {

                }
            });
            //记录日志 单向发送
            Message logMessage = new Message(JmsConfig.orderProducerTopic,"logs","发送日志".getBytes(RemotingHelper.DEFAULT_CHARSET));
            producer.sendOneway(logMessage, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    long id = arg.hashCode() & Integer.MAX_VALUE;
                    long index = id % mqs.size();
                    return mqs.get((int) index);
                }
            },orderCode);
        } catch (UnsupportedEncodingException e) {
            logger.error("订单接口获取用户信息 消息发送失败 转码异常");
            e.printStackTrace();
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } finally {
            /*System.out.println("停止了");
            orderMessageProducer.shutdown();*/
        }
        return null;
    }
}

package com.byyw.demo;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
/**
 * @author yangkai
 * @description mqtt客户端
 * @date 2022/2/10 9:39
 */
@Slf4j
public class MqttCustomerClient {
    private MqttClient client;

    /**
     * 客户端连接
     *
     * @param host     ip+端口
     * @param clientID 客户端Id
     * @param username 用户名
     * @param password 密码
     * @param timeout  超时时间
     * @param keeplive 保留数
     */
    public void connect(String host, String clientID, String username, String password, int timeout, int keeplive,MqttCallback mqttCallback) {
        try {
            this.client = new MqttClient(host, clientID, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(timeout);
            options.setKeepAliveInterval(keeplive);
            try {
                client.setCallback(mqttCallback);
                client.connect(options);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布，默认qos为0，非持久化
     * 
     * @param topic
     * @param pushMessage
     * @throws MqttException
     * @throws MqttPersistenceException
     */
    public void publish(String topic, String pushMessage) throws MqttPersistenceException, MqttException {
        publish(0, false, topic, pushMessage);
    }

    /**
     * 发布
     *
     * @param qos         连接方式
     * @param retained    是否保留
     * @param topic       主题
     * @param pushMessage 消息体
     * @throws MqttException
     * @throws MqttPersistenceException
     */
    public void publish(int qos, boolean retained, String topic, String pushMessage) throws MqttPersistenceException, MqttException {
        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setRetained(retained);
        message.setPayload(pushMessage.getBytes());
        MqttTopic mqttTopic = this.client.getTopic(topic);
        if (null == mqttTopic) {
            log.error("topic not exist");
            return;
        }
        MqttDeliveryToken token;
        token = mqttTopic.publish(message);
        token.waitForCompletion();
    }

    /**
     * 订阅某个主题，qos默认为0
     * 以/#结尾表示订阅所有以test开头的主题
     * mqttCustomerClient.subscribe("test/#");
     * 
     * @param topic
     */
    public void subscribe(String topic) {
        // log.error("开始订阅主题" + topic);
        subscribe(topic, 0);
    }

    public void subscribe(String topic, int qos) {
        try {
            this.client.subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
package com.byyw.demo;
 
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;
 
/**
 * @author yangkai
 * @description 消费监听
 * @date 2022/2/10 9:40
 */
@Component
public class PushCallback implements MqttCallback {
 
    private static MqttClient client;
   
    @Override
    public void connectionLost(Throwable throwable) {
        if (client == null || !client.isConnected()) {
            System.out.println("连接断开，正在重连....");
        }
    }
 
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("接收消息主题 : " + topic);
        System.out.println("接收消息Qos : " + message.getQos());
        System.out.println("接收消息内容 : " + new String(message.getPayload()));
    }
 
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("deliveryComplete---------" + token.isComplete());
    }
}
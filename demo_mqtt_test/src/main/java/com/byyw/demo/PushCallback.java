package com.byyw.demo;
 
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
 
/**
 * @author yangkai
 * @description 消费监听
 * @date 2022/2/10 9:40
 */
public class PushCallback implements MqttCallback {
    
    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("连接断开，正在重连....");
    }
 
    private long now = System.currentTimeMillis();
    private long i=0;
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        i++;
        if(System.currentTimeMillis()-now>1000){
            System.out.println("receive/s: "+i+"");
            i = 0;
            now=System.currentTimeMillis();
        }
    }
 
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // System.out.println("deliveryComplete---------" + token.isComplete());
    }
}
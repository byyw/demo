package com.byyw.demo;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**

    条件：发布数350，订阅数20，共享订阅
    最大流入 70000/s
    最大流出 70000/s
    受本机资源限制，同时运行测试程序与服务程序，cpu占满了，连接数能再上去，但消息的流入流出总值变化不大

    条件：发布数120，订阅数10
    最大流入 40000/s
    最大流出 400000/s

    条件：发布数250，订阅数200，主题数200
    最大流入 60000/s
    最大流出 60000/s

    条件：发布数15000，订阅数1，主题数1，发送间隔10000ms
    受本机资源限制，最多模拟出15000左右的连接数

    条件：发布数10000，订阅数5000，主题数5000，发送间隔1000ms
    最大流入 10000/s
    最大流出 10000/s

    条件：发布数10000，订阅数5000，主题数500，发送间隔1000ms
    最大流入 10000/s
    最大流出 100000/s

    
 */
@Component
public class Test {

    public static int sends = 10000;
    public static int receives = 5000;
    public static boolean isShare = false;
    public static int mod = 500;
    public static int wait = 1000;


    @PostConstruct
    public void init() {
        for(int t=0;t<receives;t++){
            int id = t;
            new Thread(()->{
                int idd = id;
                MqttCustomerClient mqttCustomerClient = new MqttCustomerClient();
                mqttCustomerClient.connect("tcp://192.168.1.77:10111","receive_"+idd,"admin","admin123",10,30,new MqttCallback() {
        
                    private long now = System.currentTimeMillis();
                    private long i=0;
    
                    @Override
                    public void connectionLost(Throwable throwable) {
                        mqttCustomerClient.connect("tcp://192.168.1.77:10111","receive_"+idd,"admin","admin123",10,30,this);
                        if(isShare){
                            mqttCustomerClient.subscribe("$share/myGroup/test"+idd%mod);
                        } else {
                            mqttCustomerClient.subscribe("test"+idd%mod);
                        }
                        
                    }
                 
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        i++;
                        if(System.currentTimeMillis()-now>1000){
                            // System.out.println("receive/s: "+i+"");
                            i = 0;
                            now=System.currentTimeMillis();
                        }
                    }
                 
                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });
                if(isShare){
                    mqttCustomerClient.subscribe("$share/myGroup/test"+idd%mod);
                } else {
                    mqttCustomerClient.subscribe("test"+idd%mod);
                }
            }).start();
        }

        for(int t=0;t<sends;t++){
            int id = t;
            new Thread(()->{
                int idd = id;
                MqttCustomerClient mqttCustomerClient = new MqttCustomerClient();
                mqttCustomerClient.connect("tcp://192.168.1.77:10111","send_"+idd,"admin","admin123",10,30,new MqttCallback() {
    
                    @Override
                    public void connectionLost(Throwable throwable) {
                        mqttCustomerClient.connect("tcp://192.168.1.77:10111","send_"+idd,"admin","admin123",10,30,this);
                    }
                 
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                    }
                 
                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });

                long now = System.currentTimeMillis();
                int i=0;
                while (true){
                    try {
                        mqttCustomerClient.publish("test"+idd%mod,i+"");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    i++;
                    try {
                        Thread.sleep(wait);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    if((System.currentTimeMillis()-now)>1000){
                        i = 0;
                        now=System.currentTimeMillis();
                    }
                }
            }).start();
            // if(t >= 5000){
            //     try {
            //         Thread.sleep(100);
            //     } catch (InterruptedException e) {
            //         // TODO Auto-generated catch block
            //         e.printStackTrace();
            //     }
            // }
        }
    }
    
}


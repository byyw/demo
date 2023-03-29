package com.byyw.demo;
 
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
 
/**
 * @author yangkai
 * @description mqtt订阅
 * @date 2022/2/10 9:36
 */
@Component
@Configuration
@Data
@ConfigurationProperties("mqtt")
public class MqttConfiguration {
    @Autowired
    private MqttCustomerClient mqttCustomerClient;
 
    private String host;
    private String clientid;
    private String username;
    private String password;
    private String topic;
    private int timeout;
    private int keepalive;
 
    @Bean
    public MqttCustomerClient getMqttCustomerClient() {
        mqttCustomerClient.connect(host, clientid, username, password, timeout,keepalive);
        // 以/#结尾表示订阅所有以test开头的主题
        mqttCustomerClient.subscribe("test/#");
        return mqttCustomerClient;
    }
}
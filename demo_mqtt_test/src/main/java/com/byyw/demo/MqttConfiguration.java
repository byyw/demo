package com.byyw.demo;
 
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
 
/**
 * @author yangkai
 * @description mqtt订阅
 * @date 2022/2/10 9:36
 */
@Component
@Data
@ConfigurationProperties("mqtt")
public class MqttConfiguration {
 
    private String host;
    private String clientid;
    private String username;
    private String password;
    private String topic;
    private int timeout;
    private int keepalive;
}
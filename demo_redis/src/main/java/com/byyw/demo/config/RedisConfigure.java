package com.byyw.demo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * redis缓存序列化
 * 1、使用Jackson2JsonRedisSerializer需要指明序列化的类Class，可以使用 Object.class
 * 2、使用GenericJacksonRedisSerializer比Jackson2JsonRedisSerializer效率低，占用内存高。
 * 3、GenericJacksonRedisSerializer反序列化带泛型的数组类会报转换异常，解决办法存储以JSON字符串存储。
 * 4、GenericJacksonRedisSerializer和Jackson2JsonRedisSerializer都是以JSON格式去存储数据，都可以作为Redis的序列化方式。
 *
 * @author czx
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfigure {

    // @Autowired
    // private CeloanStringSerializer celoanStringSerializer;

    /** 自定义redis序列化的机制,重新定义一个ObjectMapper.防止和MVC的冲突 */
    @Bean("redisSerializer")
    public RedisSerializer<Object> redisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 反序列化时候遇到不匹配的属性并不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时候遇到空对象不抛出异常
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 反序列化的时候如果是无效子类型,不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        // 不使用默认的dateTime进行序列化
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        // 解决jackson2无法反序列化LocalDateTime的问题
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));

        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));

        // 使用JSR310提供的序列化类,里面包含了大量的JDK8时间序列化类
        objectMapper.registerModule(javaTimeModule);
        // 启用反序列化所需的类型信息,在属性中添加@class
        // objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
        // ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        // 配置null值的序列化器
        // GenericJackson2JsonRedisSerializer.registerNullValueSerializer(objectMapper,
        // null);
        // 使用GenericJackson2JsonRedisSerializer来序列化和反序列化redis的value值
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
    
    // /** 
    //  * @param connectionFactory
    //  * @param redisSerializer
    //  * @return RedisTemplate<Object, Object>
    //  */
    // @Bean("redisTemplate")
    // public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory,
    //         RedisSerializer<Object> redisSerializer) {
    //     RedisTemplate<Object, Object> template = new RedisTemplate<>();
    //     template.setConnectionFactory(connectionFactory);
    //     template.setDefaultSerializer(redisSerializer);
    //     // 序列化 value 时使用此序列化方法
    //     template.setValueSerializer(redisSerializer);
    //     template.setHashValueSerializer(redisSerializer);
    //     // 序列化 key 时
    //     template.setKeySerializer(celoanStringSerializer);
    //     template.setHashKeySerializer(celoanStringSerializer);
    //     template.afterPropertiesSet();
    //     return template;
    // }

    
    /** 
     * @param connectionFactory
     * @param redisSerializer
     * @return RedisTemplate<Object, Object>
     */
    @Bean("globalRedisTemplate")
    public RedisTemplate<Object, Object> globalRedisTemplate(RedisConnectionFactory connectionFactory,
            RedisSerializer<Object> redisSerializer) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setDefaultSerializer(redisSerializer);
        // 序列化 value 时使用此序列化方法
        template.setValueSerializer(redisSerializer);
        template.setHashValueSerializer(redisSerializer);
        // 序列化 key 时
        StringRedisSerializer srs = new StringRedisSerializer();
        template.setKeySerializer(srs);
        template.setHashKeySerializer(srs);
        
        template.afterPropertiesSet();
        return template;
    }

    
    /** 
     * @param redisConnectionFactory
     * @return RedisMessageListenerContainer
     */
    @Bean("listenerContainer")
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory redisConnectionFactory) {
        
        // MessageListener MessageListener
        // 创建redis消息监听器容器
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        // 设置Redis连接的线程安全工厂
        listenerContainer.setConnectionFactory(redisConnectionFactory);
        // 创建一个主题  （基于模式匹配）
        // 订阅了一个主题
        // listenerContainer.addMessageListener((message,bytes)->{
        //     String body = new String(message.getBody());
        //     String channel = new String(message.getChannel());
        //     System.out.println(body);
        //     System.out.println(channel);
        // }, new PatternTopic("__keyspace@0__:test"));

        return listenerContainer;
    }
}

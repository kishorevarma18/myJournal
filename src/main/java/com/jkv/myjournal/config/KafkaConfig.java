package com.jkv.myjournal.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapServer;

    @Bean
    public KafkaAdmin kafkaAdmin(){
        Map<String,Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,bootStrapServer);
        return new KafkaAdmin(config);
    }

    @Bean
    NewTopic emailNotificationTopic(){
        return TopicBuilder.name("email-notification-events")
        .partitions(5)
        .replicas(1)
        .build();
    }
}

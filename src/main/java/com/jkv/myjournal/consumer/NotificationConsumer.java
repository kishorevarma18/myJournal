package com.jkv.myjournal.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.jkv.myjournal.event.EmailEvent;
import com.jkv.myjournal.util.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
    private final EmailService emailService;

    @KafkaListener(topics ="email-notification-events",groupId = "myjournal-notification-group")
    public void consumeEmailEvent(ConsumerRecord<String, EmailEvent> emailRecord){
        log.info("Recieved Kafka message from Email Notification Events. Partition {}. Message Key {}.",emailRecord.partition(),emailRecord.key());
        EmailEvent event = emailRecord.value();
        if(event!=null){
            try{
                emailService.sendEmail(event.getTo(), event.getSubject(), event.getBody());
            }
            catch(Exception e){
                log.error("Error executing background email worker task for key {}: {}", emailRecord.key(), e.getMessage());
            }
        }
        
    }
}

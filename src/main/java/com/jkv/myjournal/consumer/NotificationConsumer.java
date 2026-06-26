package com.jkv.myjournal.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
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
    /*
    added retry and dead letter mechanism.
    if there is an error while consuming, it will retry for 2 time for intervals 5 sec and 10 sec.
    if it is still giving error then it will got to dead letter Topic and execute the code in dead letter.
    */
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 5000, multiplier = 2.0),
        include = {MailAuthenticationException.class}
        //dltTopicSuffix = "-dtl"
        
    )
    @KafkaListener(topics ="email-notification-events",groupId = "myjournal-notification-group")
    public void consumeEmailEvent(ConsumerRecord<String, EmailEvent> emailRecord){
        log.info("Recieved Kafka message from Email Notification Events. Partition {}. Message Key {}.",emailRecord.partition(),emailRecord.key());
        EmailEvent event = emailRecord.value();
        if(event!=null){
            emailService.sendEmail(event.getTo(), event.getSubject(), event.getBody());
        }
    }

    //added dead letter Topic implementation.
    @DltHandler
    public void handleDeadLetter(
        EmailEvent event,
        @Header(KafkaHeaders.RECEIVED_TOPIC)
        String topic,
        @Header(KafkaHeaders.EXCEPTION_MESSAGE)
        String exception,
        @Header(KafkaHeaders.ORIGINAL_OFFSET)
        long offset,
        @Header(KafkaHeaders.ORIGINAL_PARTITION)
        int partition) {
        log.error(" Email moved to DLT \nTopic      : {}\nPartition  : {}\nOffset     : {}\nRecipient  : {}\nSubject    : {}\nError      : {}",
        topic,
        partition,
        offset,
        event.getTo(),
        event.getSubject(),
        exception
    );
}
}

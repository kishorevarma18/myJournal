package com.jkv.myjournal.scheduler;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jkv.myjournal.event.EmailEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
//since this is not a business logic we mentioned @Component here instead of @Service.
@Slf4j
@RequiredArgsConstructor
public class JournalScheduler {
    private final KafkaTemplate<String, EmailEvent> kafkaTemplate;

    @Scheduled(cron = "0 0 0 * * *")
    //@Scheduled is used for scheduling the cron job.
    public void executeDailyMaintenance(){
        log.info("Started Daily Maintenance scheduled cron job");

        try{
            log.info("Daily maintenance completed successfully.");
            StringBuilder body = new StringBuilder();
            body.append("Hi Admin,\n")
                .append("\nDaily maintenance cron job completed!")
                .append("\nThank you,\n")
                .append("myjournal");
            EmailEvent event = EmailEvent.builder()
                .to("jkv9963@gmail.com")
                .subject("Daily Maintence Report")
                .body(body.toString())
                .build();
            kafkaTemplate.send("email-notification-events","DailyMaintenceReport",event);
        }
        catch(Exception e){
            log.error("Error occurred during daily maintenance cron execution: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    public void executeHourlyCacheEviction() {
        log.info("Executing hourly clean up task...");
        log.info("Clean up completed.");
    }
}

package io.howeveryir.talentflow.mailserver;

import io.howeveryir.talentflow.model.MailConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TalentFlowMailApplication {

    public static void main(String[] args) {
        SpringApplication.run(TalentFlowMailApplication.class, args);
    }

    @Bean
    Queue queue() {
        return new Queue(MailConstants.MAIL_QUEUE_NAME);
    }
}

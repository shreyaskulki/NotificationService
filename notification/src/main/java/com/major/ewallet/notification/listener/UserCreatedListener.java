package com.major.ewallet.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.major.ewallet.notification.model.NotificationUser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserCreatedListener {

    private static final String USER_CREATED = "USER_CREATED";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    String systemUser;

    @SneakyThrows
    @KafkaListener(topics = {USER_CREATED},groupId = "notification_group")
    public void receivedMessage(@Payload String message){
        log.info("****** SENDING EMAIL TO NEW CREATED USER ***** "+message);

        NotificationUser notificationUser = objectMapper.readValue(message,NotificationUser.class);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(notificationUser.getEmail());
        simpleMailMessage.setFrom(systemUser);
        simpleMailMessage.setSubject("Welcome to Mango Ewallet Application! ");
        simpleMailMessage.setText("Hello, "+notificationUser.getName()+" your account is successfully created with Mango Ewallet application! ");
        javaMailSender.send(simpleMailMessage);
    }
}

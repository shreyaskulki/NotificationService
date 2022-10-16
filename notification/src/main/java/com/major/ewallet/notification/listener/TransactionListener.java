package com.major.ewallet.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.major.ewallet.notification.model.NotificationUser;
import com.major.ewallet.notification.model.TransactionDetail;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
@Slf4j
public class TransactionListener {

    private static final String TRANSACTION_SUCCESS="TRANSACTION_SUCCESS";

    private static final String TRANSACTION_FAILURE="TRANSACTION_FAILURE";

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${pocketbook.user.systemId}")
    Long systemId;

    @Value("${spring.mail.username}")
    String systemUser;

    @Autowired
    JavaMailSender javaMailSender;

    @SneakyThrows
    @KafkaListener(topics = {TRANSACTION_SUCCESS},groupId = "notification_group")
    public void sendSuccessEmail(@Payload String message){
        TransactionDetail transactionDetail = objectMapper.readValue(message,TransactionDetail.class);

        if(!Objects.equals(transactionDetail.getSenderId(),systemId)){
            /*
             * SEND SUCCESS  EMAIL*/
            log.info("*** SENDING TO DEBIT EMAIL ***");
            NotificationUser sender = getUserById(transactionDetail.getSenderId());
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(sender.getEmail());
            simpleMailMessage.setFrom(systemUser);
            simpleMailMessage.setSubject("Your account has been debited "+transactionDetail.getAmount());
            simpleMailMessage.setText("Hello, "+sender.getName()+" your account has been debited by "+transactionDetail.getAmount());
            javaMailSender.send(simpleMailMessage);

        }

        if(!Objects.equals(transactionDetail.getReceiverId(),systemId)){
            /*
             * SEND SUCCESS  EMAIL*/
            log.info("*** SENDING TO CREDIT EMAIL ***");
            NotificationUser receiver = getUserById(transactionDetail.getReceiverId());
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(receiver.getEmail());
            simpleMailMessage.setFrom(systemUser);
            simpleMailMessage.setSubject("Your account has been credited "+transactionDetail.getAmount());
            simpleMailMessage.setText("Hello, "+receiver.getName()+" your account has been credited by "+transactionDetail.getAmount());
            javaMailSender.send(simpleMailMessage);

        }
    }

    @SneakyThrows
    @KafkaListener(topics = {TRANSACTION_FAILURE},groupId = "notification_group")
    public void sendFailureEmail(@Payload String message){
        TransactionDetail transactionDetail = objectMapper.readValue(message,TransactionDetail.class);

        if(!Objects.equals(transactionDetail.getSenderId(),systemId)){
            /*
            * SEND FAILURE EMAIL*/
            NotificationUser sender = getUserById(transactionDetail.getSenderId());
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(sender.getEmail());
            simpleMailMessage.setFrom(systemUser);
            simpleMailMessage.setSubject("Your account has been debited "+transactionDetail.getAmount());
            simpleMailMessage.setText("Hello, "+sender.getName()+" your recent transaction for "+transactionDetail.getAmount()+" has failed");
            javaMailSender.send(simpleMailMessage);

        }
    }

    @SneakyThrows
    public NotificationUser getUserById(Long id){
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8080/user/"+id,String.class);

        return objectMapper.readValue(responseEntity.getBody(),NotificationUser.class);
    }
}

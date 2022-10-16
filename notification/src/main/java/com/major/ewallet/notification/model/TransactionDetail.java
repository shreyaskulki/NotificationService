package com.major.ewallet.notification.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(value = {"createdAt","updatedAt","status"})
public class TransactionDetail {
    Long id;
    Long senderId;
    Long receiverId;
    Double amount;
}

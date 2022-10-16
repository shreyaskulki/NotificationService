package com.major.ewallet.notification.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class NotificationUser {

    private Long id;
    private String name;
    private String email;
    private String contactNumber;
    private String dob;
    private String createdAt;
    private String updatedAt;

}

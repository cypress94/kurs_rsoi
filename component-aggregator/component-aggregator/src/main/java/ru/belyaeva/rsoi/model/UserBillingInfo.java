package ru.belyaeva.rsoi.model;

import lombok.Data;


/**
 * Created by user on 16.11.2016.
 */
@Data
public class UserBillingInfo {

    private String meansPayment;

    private String cardNumber;

    private String CVV;

    private Double Summary;

    private Long UserId;
}
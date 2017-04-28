package ru.belyaeva.rsoi.model;

import lombok.Data;

/**
 * Created by user on 18.12.2016.
 */
@Data
public class BillingResponse {
    private String billingTime;
    private Double summary;
    private String meansPayment;
}

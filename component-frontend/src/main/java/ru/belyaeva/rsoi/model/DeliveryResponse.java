package ru.belyaeva.rsoi.model;

import lombok.Data;

/**
 * Created by user on 18.12.2016.
 */
@Data
public class DeliveryResponse {
    private Delivery Delivery;
    private Boolean code;
    private String Message;
}

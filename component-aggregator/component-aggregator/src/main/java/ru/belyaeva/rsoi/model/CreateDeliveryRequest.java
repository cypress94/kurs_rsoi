package ru.belyaeva.rsoi.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by user on 18.12.2016.
 */
@Data
public class CreateDeliveryRequest {
    private Long userId;
    private String Name;
    private Double Weight;
    private Double Width;
    private Double Height;
    private Double Deep;
    private String TimeDelivery;
    private String AddressStart;
    private String AddressFinish;
    private String Description;
}

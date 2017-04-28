package ru.belyaeva.rsoi.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by user on 19.12.2016.
 */
@Data
public class UpdateDeliveryRequest {
    private Long Id;
    private String Name;
    private Long   CourierId;

    private Double Width;
    private Double Weight;
    private Double Height;
    private Double Deep;
    private String TimeDelivery;
    private String AddressStart;
    private String AddressFinish;
    private String Description;
}

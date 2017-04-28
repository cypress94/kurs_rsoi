package ru.belyaeva.rsoi.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by user on 18.12.2016.
 */
@Data
public class DeliveryFull {
    private Long Id;
    private Long UserId;
    private Long CourierId;
    private Boolean Paid;
    private BillingResponse Billing;
    private List<TrackResponse> Tracks;
    private String Name;
    private Double Weight;
    private Double Width;
    private Double Height;
    private Double Deep;
    private String TimeDelivery;
    private String AddressStart;
    private String AddressFinish;
    private String Description;
    private Boolean Code;
    private String Message;
}

package ru.belyaeva.rsoi.model;

import java.util.List;
import lombok.Data;

/**
 * Created by user on 18.12.2016.
 */
@Data
public class DeliveryListResponse {
    private List<Delivery> deliveries;
    private Boolean code;
    private String Message;
}

package ru.belyaeva.rsoi.model;

import lombok.Data;

/**
 * Created by user on 19.12.2016.
 */
@Data
public class UpdateOrderRequest {
    private Long OrderId;
    private String ShopName;
    private String SellerName;
}

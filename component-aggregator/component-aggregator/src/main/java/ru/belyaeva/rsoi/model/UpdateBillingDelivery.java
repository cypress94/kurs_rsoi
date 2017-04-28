package ru.belyaeva.rsoi.model;

import lombok.Data;

/**
 * Created by user on 31.03.2017.
 */
@Data
public class UpdateBillingDelivery {
    private Boolean Paid;
    private Long BillingId;
}

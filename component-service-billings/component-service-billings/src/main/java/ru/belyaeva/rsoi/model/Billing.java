package ru.belyaeva.rsoi.model;

import lombok.Data;
import ru.belyaeva.rsoi.domain.UserEntity;

/**
 * Created by user on 18.12.2016.
 */
@Data
public class Billing {

    private Long id;

    private String billingTime;

    private Double summary;

    private UserEntity userBilling;


}
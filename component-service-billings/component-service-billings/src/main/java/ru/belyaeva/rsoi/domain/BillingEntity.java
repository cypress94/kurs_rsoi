package ru.belyaeva.rsoi.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by user on 26.11.2016.
 */
@Data
@Entity(name = "billings")
@Table
public class BillingEntity {
    @Id
    @SequenceGenerator(name = "billing_ids", sequenceName = "billing_ids",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_ids")
    private Long id;

    @Column(name = "billing_time")
    private String billingTime;

    @Column(name = "summary")
    private Double summary;

    @ManyToOne
    private UserEntity userBilling;

}

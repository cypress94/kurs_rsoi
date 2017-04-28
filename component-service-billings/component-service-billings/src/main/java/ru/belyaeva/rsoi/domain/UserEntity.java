package ru.belyaeva.rsoi.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by user on 28.03.2017.
 */
@Data
@Entity(name = "user_billings")
@Table
public class UserEntity {
    @Id
    @SequenceGenerator(name = "user_ids", sequenceName = "user_ids",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_ids")
    private Long id;

    @Column(name = "means_of_payment")
    private String meansPayment;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "CVV")
    private String CVV;

    @Column(name = "summary")
    private Double Summary;

    @Column(name = "userId")
    private Long userId;
}

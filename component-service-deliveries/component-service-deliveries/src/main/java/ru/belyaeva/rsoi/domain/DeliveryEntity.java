package ru.belyaeva.rsoi.domain;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 26.11.2016.
 */
@Data
@Entity(name = "deliveries")
@Table
public class DeliveryEntity {
    @Id
    @SequenceGenerator(name = "delivery_ids", sequenceName = "delivery_ids",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_ids")
    private Long Id;

    @Column(name = "paid")
    private Boolean Paid;

    @Column(name = "userid")
    private Long userId;

    @OneToOne
    @JoinColumn(name = "billing")
    private BillingEntity Billing;

    @Column(name = "Name")
    private String Name;

    @Column(name = "weight")
    private Double Weight;

    @Column(name = "width")
    private Double Width;

    @Column(name = "height")
    private Double Height;

    @Column(name = "deep")
    private Double Deep;

    @Column(name = "time_delivery")
    private Date TimeDelivery;

    @Column(name = "address_start")
    private String AddressStart;

    @Column(name = "address_finish")
    private String AddressFinish;

    @Column(name = "description")
    private String Description;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(
            name = "tracks",
            joinColumns = @JoinColumn(name = "track_id")
    )
    private List<Long> Tracks;

}

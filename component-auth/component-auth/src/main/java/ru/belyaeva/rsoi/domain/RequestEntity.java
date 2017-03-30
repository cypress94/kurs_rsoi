package ru.belyaeva.rsoi.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by user on 22.11.2016.
 */
@Data
@Entity(name = "requests")
@Table
public class RequestEntity {
    @Id
    @SequenceGenerator(name = "request_ids", sequenceName = "request_ids",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_ids")
    private Long id;

    @ManyToOne
    private UserEntity user;

    @Column(name = "code")
    private String code;

    @Column(name = "redirect_uri")
    private String redirectUri;


}

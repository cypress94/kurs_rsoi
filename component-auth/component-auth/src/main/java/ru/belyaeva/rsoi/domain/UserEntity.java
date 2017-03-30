package ru.belyaeva.rsoi.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by user on 15.11.2016.
 */
@Data
@Entity(name = "users")
@Table
public class UserEntity {

    @Id
    @SequenceGenerator(name = "user_ids", sequenceName = "user_ids",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_ids")
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "isCourier")
    private Boolean isCourier;

    @Column(name = "description")
    private String description;

    @Column(name = "firstName")
    private String firstname;

    @Column(name = "lastName")
    private String lastname;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;


}

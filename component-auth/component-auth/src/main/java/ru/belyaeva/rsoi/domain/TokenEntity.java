package ru.belyaeva.rsoi.domain;


/**
 * Created by user on 13.11.2016.
 */

import lombok.Data;

import javax.persistence.*;


@Data
@Entity(name = "tokens")
@Table
public class TokenEntity {

    @Id
    @SequenceGenerator(name = "token_ids", sequenceName = "token_ids",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_ids")
    private Long id;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "token_type")
    private String tokenType;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "expires_in")
    private Long expiresIn;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public TokenEntity(Long id, String email, String firstname, String accessToken, String refreshToken,
            Long expiresIn) {

        this.user.setEmail(email);
        this.setId(id);
        this.user.setFirstname(firstname);
        this.setAccessToken(accessToken);
        this.setRefreshToken(refreshToken);
        this.setExpiresIn(expiresIn);
    }

    public TokenEntity() {
    }
}

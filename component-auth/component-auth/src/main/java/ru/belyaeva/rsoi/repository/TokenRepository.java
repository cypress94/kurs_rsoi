package ru.belyaeva.rsoi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.belyaeva.rsoi.domain.TokenEntity;

/**
 * Created by user on 13.11.2016.
 */
@Repository
public interface TokenRepository extends CrudRepository<TokenEntity,Long> {
    TokenEntity findByAccessToken(String accessToken);

    TokenEntity findByRefreshToken(String refreshToken);
}

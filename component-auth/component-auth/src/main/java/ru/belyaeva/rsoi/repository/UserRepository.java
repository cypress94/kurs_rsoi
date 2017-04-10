package ru.belyaeva.rsoi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.belyaeva.rsoi.domain.UserEntity;

/**
 * Created by user on 15.11.2016.
 */
@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    UserEntity findByClientId(String clientId);
}

package ru.belyaeva.rsoi.repository;

import org.springframework.data.repository.CrudRepository;
import ru.belyaeva.rsoi.domain.UserEntity;

/**
 * Created by user on 26.11.2016.
 */
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    UserEntity findByUserId(Long UserId);

}

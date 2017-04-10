package ru.belyaeva.rsoi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.belyaeva.rsoi.domain.RequestEntity;

/**
 * Created by user on 22.11.2016.
 */
@Repository
public interface RequestRepository extends CrudRepository<RequestEntity,Long> {
    RequestEntity findById(Long id);

    RequestEntity findByCode(String code);

}

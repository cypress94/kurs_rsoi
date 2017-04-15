package ru.belyaeva.rsoi.repository;

import org.springframework.data.repository.CrudRepository;
import ru.belyaeva.rsoi.domain.DeliveryEntity;

import java.util.List;

/**
 * Created by user on 26.11.2016.
 */
public interface DeliveryRepository extends CrudRepository<DeliveryEntity,Long> {

    List<DeliveryEntity> findByUserId(Long UserId);
    List<DeliveryEntity> findByCourierId(Long CourierId);

}

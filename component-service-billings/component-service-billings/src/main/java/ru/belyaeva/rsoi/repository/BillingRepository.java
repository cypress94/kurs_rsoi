package ru.belyaeva.rsoi.repository;

import org.springframework.data.repository.CrudRepository;
import ru.belyaeva.rsoi.domain.BillingEntity;

/**
 * Created by user on 26.11.2016.
 */
public interface BillingRepository extends CrudRepository<BillingEntity, Long> {
    BillingEntity findById(Long Id);
}

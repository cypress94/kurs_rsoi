package ru.belyaeva.rsoi.repository;

import org.springframework.data.repository.CrudRepository;
import ru.belyaeva.rsoi.domain.TrackEntity;

/**
 * Created by user on 26.11.2016.
 */
public interface TrackRepository extends CrudRepository<TrackEntity,Long> {
    TrackEntity findById(Long trackId);
    void delete(Long trackId);

}

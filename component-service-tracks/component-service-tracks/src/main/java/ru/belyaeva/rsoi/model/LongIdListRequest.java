package ru.belyaeva.rsoi.model;
import java.util.List;
import lombok.Data;

/**
 * Created by user on 19.12.2016.
 */
@Data
public class LongIdListRequest {
    private List<Long> ids;
}

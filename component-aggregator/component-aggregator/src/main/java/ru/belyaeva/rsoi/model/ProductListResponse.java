package ru.belyaeva.rsoi.model;
import lombok.Data;

import java.util.List;
/**
 * Created by user on 19.12.2016.
 */
@Data
public class ProductListResponse {
    private List<TrackResponse> products;
    private Boolean Code;
    private String Message;
}

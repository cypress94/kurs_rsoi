package ru.belyaeva.rsoi.model;
import lombok.Data;

import java.util.List;
/**
 * Created by user on 19.12.2016.
 */
@Data
public class TracksListResponse {
    private List<TrackResponse> tracks;
    private Boolean Code;
    private String Message;
}

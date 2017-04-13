package ru.belyaeva.rsoi.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by user on 19.12.2016.
 */
@Data
public class TrackResponse {
    private Long id;
    private String trackMessage;
    private Date dateMessage;
    private Boolean Code;
    private String Message;
}

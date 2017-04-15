package ru.belyaeva.rsoi.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by user on 22.03.2017.
 */
@Data
public class Track {
    private Long id;
    private String trackMessage;
    private Date dateMessage;
}

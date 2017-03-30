package ru.belyaeva.rsoi.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by user on 26.11.2016.
 */
@Data
@Entity(name = "tracks")
@Table
public class TrackEntity {
    @Id
    @SequenceGenerator(name = "track_ids", sequenceName = "track_ids",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "track_ids")
    private Long id;

    @Column(name = "message")
    private String trackMessage;

    @Column(name = "date_message")
    private Date dateMessage;

}

package ru.belyaeva.rsoi.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.model.*;
import ru.belyaeva.rsoi.repository.TrackRepository;
import ru.belyaeva.rsoi.domain.TrackEntity;

import javax.persistence.EntityNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.*;

/**
 * Created by user on 22.11.2016.
 */
@Service
public class TrackServiceImpl implements TrackService {

    @Autowired
    TrackRepository trackRepository;


    @SneakyThrows
    @Override
    public TracksListResponse getTracks(LongIdListRequest listid) {

        List<TrackEntity> trackEntities = (List<TrackEntity>) trackRepository.findAll();
        List<Long> arrayTracks = listid.getIds();
        List<TrackResponse> tracks = new ArrayList<>();

        for (Long id : arrayTracks){
            TrackResponse trackResponse = new TrackResponse();
            TrackEntity trackEntity = trackRepository.findById(id);
            if (trackEntity == null)
            {
                trackResponse.setCode(false);
                trackResponse.setMessage("message not found");
            }else {
                trackResponse.setId(id);
                trackResponse.setTrackMessage(trackEntity.getTrackMessage());
                trackResponse.setDateMessage(trackEntity.getDateMessage());
                trackResponse.setCode(true);
                trackResponse.setMessage("");
            }
            tracks.add(trackResponse);
        }

        TracksListResponse tracksListResponse = new TracksListResponse();
        tracksListResponse.setTracks(tracks);
        tracksListResponse.setCode(true);
        tracksListResponse.setMessage("");

        return tracksListResponse;
    }

    @SneakyThrows
    @Override
    public TrackResponse getTrack(Long id) {
        TrackResponse trackResponse = new TrackResponse();
        TrackEntity trackEntity = trackRepository.findById(id);
        if (trackEntity == null)
            throw new Exception("Product with id = " + id+ " is not");
        trackResponse.setId(id);
        trackResponse.setTrackMessage(trackEntity.getTrackMessage());
        trackResponse.setDateMessage(trackEntity.getDateMessage());
        trackResponse.setCode(true);
        trackResponse.setMessage("");

        return trackResponse;
    }
    @SneakyThrows
    @Override
    public TrackResponse setTrack(Track track)
    {
        TrackEntity trackEntity = new TrackEntity();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Date date = new Date();
        trackEntity.setTrackMessage(track.getTrackMessage());
        trackEntity.setDateMessage(date);

        trackRepository.save(trackEntity);

        TrackResponse trackResponse = new TrackResponse();
        trackResponse.setId(trackEntity.getId());
        trackResponse.setTrackMessage(trackEntity.getTrackMessage());
        trackResponse.setDateMessage(trackEntity.getDateMessage());
        trackResponse.setCode(true);
        trackResponse.setMessage("");
        return trackResponse;
    }

    // + | -
    @Override
    public BaseResponse deleteTrack(Long trackId) {

        TrackEntity trackEntity = trackRepository.findOne(trackId);
        if (trackEntity == null) {
            throw new EntityNotFoundException("Message with id = " + trackId + " is not found");
        }
        trackRepository.delete(trackId);


        return new BaseResponse(true, "");
    }


  /*  @SneakyThrows
    @Override
    public void init()
    {
        TrackEntity trackEntity1 = new TrackEntity();
        long ch = 1;
        trackEntity1.setProductName("dress black");
        trackEntity1.setPrice(2000.0);
        trackEntity1.setId(ch);
        trackRepository.save(trackEntity1);
        TrackEntity trackEntity2 = new TrackEntity();
        trackEntity2.setProductName("sweater gold");
        trackEntity2.setPrice(3999.0);
        ch = 2;
        trackEntity2.setId(ch);
        trackRepository.save(trackEntity2);
        TrackEntity trackEntity3 = new TrackEntity();
        trackEntity3.setProductName("jeans stretch");
        trackEntity3.setPrice(3999.0);
        ch = 3;
        trackEntity3.setId(ch);
        trackRepository.save(trackEntity3);
        TrackEntity trackEntity4 = new TrackEntity();
        trackEntity4.setProductName("jeans classic");
        trackEntity4.setPrice(2999.0);
        ch = 4;
        trackEntity4.setId(ch);
        trackRepository.save(trackEntity4);
    }*/


}

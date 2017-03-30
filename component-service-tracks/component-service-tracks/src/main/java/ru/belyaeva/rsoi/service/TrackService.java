package ru.belyaeva.rsoi.service;

import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.model.LongIdListRequest;
import ru.belyaeva.rsoi.model.TracksListResponse;
import ru.belyaeva.rsoi.model.TrackResponse;
import ru.belyaeva.rsoi.model.BaseResponse;
import ru.belyaeva.rsoi.model.Track;

/**
 * Created by user on 22.11.2016.
 */
@Service
public interface TrackService {

    public TracksListResponse getTracks(LongIdListRequest listid);
    public TrackResponse getTrack(Long id);
    public TrackResponse setTrack(Track track);
    public BaseResponse deleteTrack(Long trackId);
}

package ru.belyaeva.rsoi.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.belyaeva.rsoi.model.Track;
import ru.belyaeva.rsoi.model.LongIdListRequest;
import ru.belyaeva.rsoi.model.TracksListResponse;
import ru.belyaeva.rsoi.model.TrackResponse;
import ru.belyaeva.rsoi.model.BaseResponse;
import ru.belyaeva.rsoi.service.TrackServiceImpl;

/**
 * Created by user on 22.11.2016.
 */
@Controller
@RequestMapping("/trackService/tracks")
public class TrackController {

    @Autowired
    TrackServiceImpl trackServiceImpl;
    /*
    @RequestMapping(value = "/{page}/{size}", method = RequestMethod.GET)
    @ResponseBody
    public String getProducts(
            @PathVariable("page") Long page,
            @PathVariable("size") Long size) {

        return trackServiceImpl.getTracks(page, size);
    }
*/
    @RequestMapping(value = "/{trackId}", method = RequestMethod.GET)
    @ResponseBody
    public TrackResponse getTrack(@PathVariable("trackId") Long trackId) {

        return trackServiceImpl.getTrack(trackId);
    }

    @RequestMapping(value = "/{trackId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteTrack(@PathVariable("trackId") Long trackId) {

        trackServiceImpl.deleteTrack(trackId);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public TracksListResponse getTracks(@RequestBody LongIdListRequest listId) {

        return trackServiceImpl.getTracks(listId);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public TrackResponse setTrack(@RequestBody Track track) {

        return trackServiceImpl.setTrack( track);
    }
/*
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public void getProduct() {
        trackServiceImpl.init();
    }

*/
}

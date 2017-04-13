package ru.belyaeva.rsoi.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.belyaeva.rsoi.model.*;
import ru.belyaeva.rsoi.service.DeliveryServiceImpl;

/**
 * Created by user on 22.11.2016.
 */
@Controller
@RequestMapping("/deliveryService/deliveries")
public class DeliveryController {

    @Autowired
    DeliveryServiceImpl deliveryServiceImpl;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public DeliveryResponse createDelivery(@RequestBody CreateDeliveryRequest request) {

        return deliveryServiceImpl.createDelivery(request);
    }

    @RequestMapping(value = "/{userId}/{page}/{size}", method = RequestMethod.GET)
    @ResponseBody
    public DeliveryListResponse getDeliveries(
            @PathVariable("userId") Long userId,
            @PathVariable("page") Long page,
            @PathVariable("size") Long size) {

        return deliveryServiceImpl.getDeliveries(userId, page, size);
    }

    @RequestMapping(value = "/{deliveryId}", method = RequestMethod.GET)
    @ResponseBody
    public DeliveryResponse getDelivery(@PathVariable("deliveryId") Long deliveryId) {

        return deliveryServiceImpl.getDelivery(deliveryId);
    }

    @RequestMapping(value = "/{deliveryId}/billing", method = RequestMethod.POST)
    @ResponseBody
    public DeliveryResponse updateBillingDelivery(
            @PathVariable("deliveryId") Long deliveryId,
            @RequestBody UpdateBillingDelivery updateBillingDelivery) {

        return deliveryServiceImpl.updateBillingDelivery(deliveryId, updateBillingDelivery);
    }



    @RequestMapping(value = "/{deliveryId}", method = RequestMethod.POST)
    @ResponseBody
    public DeliveryResponse updateDelivery(
            @PathVariable("deliveryId") Long deliveryId,
            @RequestBody UpdateDeliveryRequest request) {
        return deliveryServiceImpl.updateDelivery(request);
    }

    @RequestMapping(value = "/{deliveryId}/tracks/{trackId}", method = RequestMethod.POST)
    @ResponseBody
    public DeliveryResponse putTrackToDelivery(
            @PathVariable("deliveryId") Long deliveryId,
            @PathVariable("trackId") Long trackId,
            @RequestBody RequestNull request)
    {
        return deliveryServiceImpl.putToDelivery(deliveryId, trackId);
    }
    //  "/deleteFromOrder/{orderId}/{productId}"
    @RequestMapping(value = "/{deliveryId}/tracks/{trackId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteTrackFromDelivery(
            @PathVariable("deliveryId") Long deliveryId,
            @PathVariable("trackId") Long trackId) {
        DeliveryResponse deliveryResponse = deliveryServiceImpl.deleteFromDelivery(deliveryId, trackId);
    }
/*
    @RequestMapping(value = "/{deliveryId}/billing", method = RequestMethod.POST)
    @ResponseBody
    public DeliveryResponse executeBilling(
            @RequestBody BillingInfo billingInfo,
            @PathVariable("deliveryId") Long deliveryId) {
        return deliveryServiceImpl.executeBilling(deliveryId, billingInfo);
    }
*/
}

package ru.belyaeva.rsoi.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.belyaeva.rsoi.model.*;
import ru.belyaeva.rsoi.service.AggregationService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 22.11.2016.
 */
@Controller
public class AggregationController {

    @Autowired
    AggregationService aggregationService;

    @RequestMapping(value = "/delivery", method = RequestMethod.POST)
    @ResponseBody
    public Delivery createDelivery(
            @RequestParam(value = "name") String Name,
            @RequestParam(value = "weight") Double weight,
            @RequestParam(value = "width") Double width,
            @RequestParam(value = "height") Double height,
            @RequestParam(value = "deep") Double deep,
            @RequestParam(value = "timedelivery") String timedelivery,
            @RequestParam(value = "address_start") String address_start,
            @RequestParam(value = "address_finish") String address_finish,
            @RequestParam(value = "description") String description,
            @RequestHeader(value = "Authorization") String token) {

        Delivery delivery = new Delivery();
        delivery.setName(Name);
        delivery.setWeight(weight);
        delivery.setWidth(width);
        delivery.setHeight(height);
        delivery.setDeep(deep);
        delivery.setTimeDelivery(timedelivery);
        delivery.setAddressStart(address_start);
        delivery.setAddressFinish(address_finish);
        delivery.setDescription(description);

        return aggregationService.createDelivery(token, delivery);

    }

    @RequestMapping(value = "/delivery/{id}",
            method = RequestMethod.GET)
    @ResponseBody
    public DeliveryFull getDelivery(
            @PathVariable("id") Long orderId,
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.getDelivery(token, orderId);
    }

    @RequestMapping(value = "/delivery/{id}",
            method = RequestMethod.DELETE)
    @ResponseBody
    public BaseResponse deleteDelivery(
            @PathVariable("id") Long deliveryId,
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.deleteDelivery(token, deliveryId);
    }

    @RequestMapping(value = "/track/{id}",
            method = RequestMethod.GET)
    @ResponseBody
    public TrackResponse getProduct(
            @PathVariable("id") Long trackId,
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.getTrack(token, trackId);
    }

    @RequestMapping(value = "/delivery/{deliveryId}/track", method = RequestMethod.POST)
    @ResponseBody
    public TrackResponse createTrack(
            @RequestParam(value = "track_message") String trackMessage,
            @PathVariable("deliveryId") Long deliveryId,
            @RequestHeader(value = "Authorization") String token) {

        Track track = new Track();
        track.setTrackMessage(trackMessage);
        // время сообщение устанавливается в сервисе трека

        return aggregationService.createTrack(token, deliveryId, track);
    }
    @RequestMapping(value = "/delivery/{deliveryId}/track/{trackId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteTrack(@RequestBody Track track,
                                     @PathVariable("deliveryId") Long deliveryId,
                                     @PathVariable("trackId") Long trackId,
                                     @RequestHeader(value = "Authorization") String token) {

        aggregationService.deleteTrack(token, deliveryId, trackId);
    }

    @RequestMapping(value = "/deliveries",
            method = RequestMethod.GET)
    @ResponseBody
    public List<Delivery> getDeliveries(
            @RequestParam(value = "page") Long page,
            @RequestParam(value = "size") Long size,
            @RequestHeader(value = "Authorization") String token) {

        if (page == 0 || size == 0) {
            throw new IllegalArgumentException("Page and size can`t be 0");
        }
        return aggregationService.getDeliveriesOfUser(token, page, size);
    }

    @RequestMapping(value = "/deliveries/courier/null",
            method = RequestMethod.GET)
    @ResponseBody
    public List<Delivery> getDeliveriesWithoutCourier(
            @RequestParam(value = "page") Long page,
            @RequestParam(value = "size") Long size,
            @RequestHeader(value = "Authorization") String token) {

        if (page == 0 || size == 0) {
            throw new IllegalArgumentException("Page and size can`t be 0");
        }
        return aggregationService.getDeliveriesWithoutCourier(token, page, size);
    }
    @RequestMapping(value = "/delivery/{id}",
            method = RequestMethod.POST)
    @ResponseBody
    public DeliveryFull reservedDeliveryByCourier(
            @PathVariable("id") Long deliveryId,
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.reservedDeliveryByCourier(token, deliveryId);
    }


    @RequestMapping(value = "billing/user/create", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse createUserBilling(
            @RequestParam(value = "means_payment") String meansPayment,
            @RequestParam(value = "card_number") String cardNumber,
            @RequestParam(value = "cvv") String CVV,
            @RequestParam(value = "summary") Double Summary,
            @RequestHeader(value = "Authorization") String token) {

        UserBillingInfo userBillingInfo = new UserBillingInfo();
        userBillingInfo.setMeansPayment(meansPayment);
        userBillingInfo.setCardNumber(cardNumber);
        userBillingInfo.setCVV(CVV);
        userBillingInfo.setSummary(Summary);

        return aggregationService.createUserBilling(userBillingInfo, token);
    }

    @RequestMapping(value = "/delivery/{id}/billing",
            method = RequestMethod.POST)
    @ResponseBody
    public BillingResponse executeBilling(
            @PathVariable("id") Long deliveryId,
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.executeBilling(deliveryId, token);
    }

    @RequestMapping(value = "/delivery/{id}/billing/return",
            method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse returnBilling(
            @PathVariable("id") Long deliveryId,
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.returnBilling(deliveryId, token);
    }

    @RequestMapping(value = "/delivery/{id}/cost",
            method = RequestMethod.GET)
    @ResponseBody
    public Double getCostOfDelivery(
            @PathVariable("id") Long deliveryId,
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.getCostOfDelivery(deliveryId, token);
    }

    @RequestMapping(value = "/user/type",
            method = RequestMethod.GET)
    @ResponseBody
    public Boolean getUserType(
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.getUserType( token);
    }

    @RequestMapping(value = "/user/id",
            method = RequestMethod.GET)
    @ResponseBody
    public Long getUserId(
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.getUserId( token);
    }


    @RequestMapping(value = "/oauth2/authorization", method = RequestMethod.GET)
    public void getAuth(
            @RequestParam(value = "client_id") String clientId,
            @RequestParam(value = "redirect_uri") String redirectUri,
            @RequestParam(value = "response_type") String responseType,
            @RequestHeader(value = "Authorization", required = false) String credentials) {

        aggregationService.Autorization(redirectUri, responseType, clientId);
    }

    @RequestMapping(value = "/oauth2/token",
            params = {"client_id", "client_secret", "redirect_uri", "grant_type", "code"},
            method = RequestMethod.POST)
    @ResponseBody
    public String getToken(
            @RequestParam(value = "client_id") String clientId,
            @RequestParam(value = "client_secret") String clientSecret,
            @RequestParam(value = "redirect_uri") String redirectUri,
            @RequestParam(value = "grant_type") String grantType,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "refresh_token", required = false) String refreshToken,
            @RequestHeader(value = "Authorization", required = false) String credentials) {

        return aggregationService.getToken(clientId, clientSecret, redirectUri, code);
    }


    @RequestMapping(value = "/",
            method = RequestMethod.GET)
    @ResponseBody
    public void getUser_with_auth() {

        aggregationService.getUser_with_auth();
    }

}

package ru.belyaeva.rsoi.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.belyaeva.rsoi.model.Track;
import ru.belyaeva.rsoi.service.AggregationService;
import ru.belyaeva.rsoi.model.Delivery;
import ru.belyaeva.rsoi.model.DeliveryFull;
import ru.belyaeva.rsoi.model.TrackResponse;
;

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
    public Delivery createDelivery(@RequestBody Delivery delivery,
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.createDelivery(token, delivery);
    }

    @RequestMapping(value = "/delivery/{id}",
            method = RequestMethod.GET)
    @ResponseBody
    public DeliveryFull getOrder(
            @PathVariable("id") Long orderId,
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.getDelivery(token, orderId);
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
    public TrackResponse createTrack(@RequestBody Track track,
            @PathVariable("deliveryId") Long deliveryId,
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.createTrack(token,deliveryId, track);
    }
    @RequestMapping(value = "/delivery/{deliveryId}/track/{trackId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteTrack(@RequestBody Track track,
                                     @PathVariable("deliveryId") Long deliveryId,
                                     @PathVariable("trackId") Long trackId,
                                     @RequestHeader(value = "Authorization") String token) {

        aggregationService.deleteTrack(token, deliveryId, trackId);
    }
/*
    @RequestMapping(value = "/products",
            method = RequestMethod.GET)
    @ResponseBody
    public String getProducts(
            @RequestParam(value = "page") Long page,
            @RequestParam(value = "size") Long size,
            @RequestHeader(value = "Authorization") String token) {

        if (page == 0 || size == 0) {
            throw new IllegalArgumentException("Page and size can`t be 0");
        }
        return aggregationService.getProducts(token, page, size);
    }
*/
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
        return aggregationService.getDeliveries(token, page, size);
    }

    @RequestMapping(value = "/order/{id}",
            method = RequestMethod.POST)
    @ResponseBody
    public Delivery updateOrder(
            @PathVariable("id") Long orderId,
            @RequestParam(value = "seller_name") String sellerName,
            @RequestParam(value = "shop_name") String shopName,
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.updateOrder(orderId, sellerName, shopName, token);
    }

    @RequestMapping(value = "/order/{orderId}/product/{productId}",
            method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteFromOrder(
            @PathVariable("orderId") Long orderId,
            @PathVariable("productId") Long productId,
            @RequestHeader(value = "Authorization") String token) {

        aggregationService.deleteFromOrder(orderId, productId, token);
        return "Товар N" + productId.toString() + "удалён из заказа №"+ orderId.toString();
    }

    @RequestMapping(value = "/order/{id}/billing",
            method = RequestMethod.POST)
    @ResponseBody
    public DeliveryFull executeBilling(
            @PathVariable("id") Long orderId,
            @RequestParam(value = "means_payment") String meansPayment,
            @RequestHeader(value = "Authorization") String token) {

        return aggregationService.executeBilling(orderId, meansPayment, token);
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
    public void getProduct() {

        aggregationService.getUser();
    }

}

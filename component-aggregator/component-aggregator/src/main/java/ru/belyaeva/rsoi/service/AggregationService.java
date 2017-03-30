package ru.belyaeva.rsoi.service;

import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.model.Delivery;
import ru.belyaeva.rsoi.model.DeliveryFull;
import ru.belyaeva.rsoi.model.TrackResponse;
import ru.belyaeva.rsoi.model.Track;

import java.util.List;

/**
 * Created by user on 22.11.2016.
 *
 * Пользователь
 1.	GET user/{id}/me - получение информации об авторизованном пользователе.
 2.	GET user/delivery?page=X&size=Y - информация о заказах пользователя.
 3.	GET user/{id}/delivery/{id} - расширенная информация о заказе –информация о доставке, трек информация об оплате.
 4.	POST user/{id}/delivery- создать заказ.
 5.	PATCH user/{id}/delivery/{id} - редактировать данные заказа.
 6.	POST user/{id}/delivery/{id}/billing - выполнить биллинг (в теле передается какая-то информация для выполнения биллинга).


 Курьер
 1.	GET user/{id}/me - получение информации об авторизованном курьере.
 2.	GET user/{id}/delivery?page=X&size=Y - информация о заказах курьера.
 3.	GET user/{id}/delivery/{id} - расширенная информация о заказе –информация о доставке, трек информация об оплате.
 4.	PATCH user/{id}/delivery/track/message/{id} - редактировать данные заказа. (Редактировать трек)


 */
@Service
public interface AggregationService {
    //+
    DeliveryFull getDelivery(String token, Long id);
    //+
    Delivery createDelivery(String token, Delivery delivery);
    //+
    List<Delivery> getDeliveries (String token, Long page, Long size);

    Delivery updateOrder (Long orderId, String sellerName, String shopName, String token);

 //   void putToOrder(Long orderId, Long goodId, String token);

    void deleteFromOrder(Long orderId, Long goodId, String token);
    //+
    TrackResponse getTrack(String token, Long id);
    //+
    TrackResponse createTrack(String token, Long deliveryId, Track track);
    //+
    void deleteTrack(String token, Long deliveryId, Long trackId);

 //   String getProducts(String token, Long page, Long size);

    DeliveryFull executeBilling(Long orderId, String meansPayment, String token);

    void Autorization(String redirect_uri, String response_type, String client_id );

    String getToken(String client_id, String client_secret, String redirect_uri, String code);

    void getUser();
}

package ru.belyaeva.rsoi.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.model.*;
import ru.belyaeva.rsoi.sender.HttpSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 22.11.2016.
 */
@Service
public class AggregationServiceImpl implements AggregationService {

    @Autowired
    HttpSender httpSender;

    private static Logger logger = LoggerFactory.getLogger(AggregationServiceImpl.class);

    private final String URL_CHECK_TOKEN = "http://localhost:8080/oauth2/checkToken";
    private final String URL_GET_USER_INFO = "http://localhost:8080/user/information";
    private final String URL_SERVICE_DELIVERIES = "http://localhost:8082/deliveryService/deliveries";
    private final String URL_SERVICE_TRACKS = "http://localhost:8083/trackService/tracks";
    private final String URL_SERVICE_BILLINGS = "http://localhost:8084/billingService";
    private final String CREATE_DELIVERY = "/create";

    @SneakyThrows
    @Override
    public Boolean getUserType(String token)
    {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }

        // проверка на курьера
        UserInfoResponse userInfoResponse = httpSender.getUser(URL_GET_USER_INFO, token);
        return userInfoResponse.getIsCourier();
    }
    @SneakyThrows
    @Override
    public Long getUserId( String token)
    {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        return checkTokenResponse.getUserId();
    }

    @SneakyThrows
    @Override
    public Delivery createDelivery(String token, Delivery delivery) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }

        // проверка на курьера
        UserInfoResponse userInfoResponse = httpSender.getUser(URL_GET_USER_INFO, token);
        Boolean isCourier = userInfoResponse.getIsCourier();
      //  if (isCourier)
      //      return null;

        CreateDeliveryRequest createDeliveryRequest = new CreateDeliveryRequest();

        createDeliveryRequest.setUserId(checkTokenResponse.getUserId());
        createDeliveryRequest.setName(delivery.getName());
        createDeliveryRequest.setWeight(delivery.getWeight());
        createDeliveryRequest.setWidth(delivery.getWidth());
        createDeliveryRequest.setHeight(delivery.getHeight());
        createDeliveryRequest.setDeep(delivery.getDeep());
        createDeliveryRequest.setTimeDelivery(delivery.getTimeDelivery());
        createDeliveryRequest.setAddressStart(delivery.getAddressStart());
        createDeliveryRequest.setAddressFinish(delivery.getAddressFinish());
        createDeliveryRequest.setDescription(delivery.getDescription());

        DeliveryResponse deliveryResponse = httpSender.createDelivery(URL_SERVICE_DELIVERIES + CREATE_DELIVERY, createDeliveryRequest);
        if (!deliveryResponse.getCode()) {
            throw new Exception(deliveryResponse.getMessage());
        }
        logger.info("create Delivery");
        return deliveryResponse.getDelivery();
    }

    @SneakyThrows
    @Override
    public DeliveryFull reservedDeliveryByCourier(String token, Long deliveryId)
    {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        DeliveryResponse deliveryResponse = httpSender.getDeliveryInfo(URL_SERVICE_DELIVERIES + "/"+ String.valueOf(deliveryId));
        if (!deliveryResponse.getCode()) {
            throw new Exception(deliveryResponse.getMessage());
        }

        Delivery delivery = deliveryResponse.getDelivery();
        // проверка на курьера
        UserInfoResponse userInfoResponse = httpSender.getUser(URL_GET_USER_INFO, token);
        Boolean isCourier = userInfoResponse.getIsCourier();

         if (isCourier)
        {// обновляем данные заказа (а именно обновляем поле курьера)
            delivery.setCourierId(checkTokenResponse.getUserId());

            UpdateDeliveryRequest updateDeliveryRequest = new UpdateDeliveryRequest();
            updateDeliveryRequest.setId(deliveryId);
            updateDeliveryRequest.setCourierId(delivery.getCourierId());
            updateDeliveryRequest.setName(delivery.getName());
            updateDeliveryRequest.setWeight(delivery.getWeight());
            updateDeliveryRequest.setWidth(delivery.getWidth());
            updateDeliveryRequest.setHeight(delivery.getHeight());
            updateDeliveryRequest.setDeep(delivery.getDeep());
            updateDeliveryRequest.setTimeDelivery(delivery.getTimeDelivery());
            updateDeliveryRequest.setAddressStart(delivery.getAddressStart());
            updateDeliveryRequest.setAddressFinish(delivery.getAddressFinish());
            updateDeliveryRequest.setDescription(delivery.getDescription());

            deliveryResponse = httpSender.updateDelivery(URL_SERVICE_DELIVERIES + "/"+ String.valueOf(deliveryId), updateDeliveryRequest);
            return getDelivery(token, deliveryId);// возвращаем обновленный заказ
        }else{
             DeliveryResponse deliveryResponse1 = new DeliveryResponse();
             deliveryResponse1.setCode(false);
             deliveryResponse1.setMessage("User isn't courier");
             return null;
         }
    }

    @SneakyThrows
    @Override
    public DeliveryFull getDelivery(String token, Long id) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }

        DeliveryResponse deliveryResponse = httpSender.getDeliveryInfo(URL_SERVICE_DELIVERIES + "/"+ String.valueOf(id));
        if (!deliveryResponse.getCode()) {
            throw new Exception(deliveryResponse.getMessage());
        }
        DeliveryFull deliveryFull = new DeliveryFull();
        Delivery delivery = deliveryResponse.getDelivery();

        // проверка на курьера
        UserInfoResponse userInfoResponse = httpSender.getUser(URL_GET_USER_INFO, token);
        Boolean isCourier = userInfoResponse.getIsCourier();


        // Проверка на принадлежность заказа обычному пользователю
        if (!isCourier && delivery.getUserId() != checkTokenResponse.getUserId())
        {// ошибка, у пользователя нет такого заказа
            deliveryFull.setCode(false);
            deliveryFull.setMessage("access error");
            return deliveryFull;
        }

        if (delivery.getTracks() != null && !delivery.getTracks().isEmpty()) {
            LongIdListRequest longIdListRequest = new LongIdListRequest();
            longIdListRequest.setIds(deliveryResponse.getDelivery().getTracks());
            List<TrackResponse> tracks = new ArrayList<>();

            for (Long id_track: longIdListRequest.getIds()) {
                TrackResponse trackResponse = httpSender.getTrack(URL_SERVICE_TRACKS + "/" + id_track.toString());
                if (trackResponse != null && trackResponse.getCode()!= false)
                {
                    tracks.add(trackResponse);
                }
            }

            deliveryFull.setTracks(tracks);
        }
        if (delivery.getBilling() != null) {
            BillingResponse billingResponse = httpSender.getBilling(URL_SERVICE_BILLINGS + "/user/" + checkTokenResponse.getUserId() + "/billing/" + delivery.getBilling());
            deliveryFull.setBilling(billingResponse);
        }else{
            BillingResponse billingResponse = new BillingResponse();
            billingResponse.setCode(false);
            billingResponse.setMessage("");
            deliveryFull.setBilling(billingResponse);
        }
        deliveryFull.setId(delivery.getId());
        deliveryFull.setUserId(delivery.getUserId());
        deliveryFull.setCourierId(delivery.getCourierId());
        deliveryFull.setPaid(delivery.getPaid());
        deliveryFull.setName(delivery.getName());
        deliveryFull.setWeight(delivery.getWeight());
        deliveryFull.setWidth(delivery.getWidth());
        deliveryFull.setHeight(delivery.getHeight());
        deliveryFull.setDeep(delivery.getDeep());
        deliveryFull.setTimeDelivery(delivery.getTimeDelivery());
        deliveryFull.setAddressStart(delivery.getAddressStart());
        deliveryFull.setAddressFinish(delivery.getAddressFinish());
        deliveryFull.setDescription(delivery.getDescription());
        deliveryFull.setCode(true);
        deliveryFull.setMessage("");

        logger.info("get Delivery");
        return deliveryFull;
    }
    @SneakyThrows
    @Override
    public BaseResponse deleteDelivery(String token, Long deliveryId){
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }

        DeliveryResponse deliveryResponse = httpSender.getDeliveryInfo(URL_SERVICE_DELIVERIES + "/"+ String.valueOf(deliveryId));
        if (!deliveryResponse.getCode()) {
            throw new Exception(deliveryResponse.getMessage());
        }
        BaseResponse baseResponse = new BaseResponse();
        Delivery delivery = deliveryResponse.getDelivery();

        // проверка на курьера
        UserInfoResponse userInfoResponse = httpSender.getUser(URL_GET_USER_INFO, token);
        Boolean isCourier = userInfoResponse.getIsCourier();

        // Проверка на принадлежность заказа обычному пользователю
        if (!isCourier && delivery.getUserId() != checkTokenResponse.getUserId())
        {// ошибка, у пользователя нет такого заказа
            baseResponse.setErrorCode(false);
            baseResponse.setErrorMessage("access error");
            return baseResponse;
        }
        // удаление сообщений к заказу
        if (delivery.getTracks() != null && !delivery.getTracks().isEmpty()) {
            LongIdListRequest longIdListRequest = new LongIdListRequest();
            longIdListRequest.setIds(deliveryResponse.getDelivery().getTracks());

            for (Long id_track: longIdListRequest.getIds()) {
                httpSender.deleteTrack(URL_SERVICE_TRACKS + "/" + id_track);
            }
        }
        // удаление платёжной информации заказа
        if (delivery.getBilling() != null) {
            httpSender.deleteBilling(URL_SERVICE_BILLINGS + "/user/" + checkTokenResponse.getUserId() + "/billing/" + delivery.getBilling());
        }

        httpSender.deleteDelivery(URL_SERVICE_DELIVERIES + "/"+ String.valueOf(deliveryId));
        baseResponse.setErrorCode(true);
        baseResponse.setErrorMessage("");
        return baseResponse;

    }

    @SneakyThrows
    @Override
    public TrackResponse getTrack(String token, Long id) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        TrackResponse trackResponse = httpSender.getTrack(URL_SERVICE_TRACKS + "/" + id.toString());
        if (trackResponse == null || !trackResponse.getCode()) {
            throw new Exception(trackResponse.getMessage());
        }
        return trackResponse;
    }
    @SneakyThrows
    @Override
    public TrackResponse createTrack(String token, Long deliveryId, Track track) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        // проверка на курьера
        UserInfoResponse userInfoResponse = httpSender.getUser(URL_GET_USER_INFO, token);
        Boolean isCourier = userInfoResponse.getIsCourier();
        if (!isCourier)
        {
            TrackResponse trackResponse = new TrackResponse();

            trackResponse.setCode(false);
            trackResponse.setMessage("access privilege error");
            return trackResponse;
        }

        TrackResponse trackResponse = httpSender.createTrack(URL_SERVICE_TRACKS + "/", track);
        if (trackResponse == null || !trackResponse.getCode()) {
            throw new Exception(trackResponse.getMessage());
        }

        // добавление трека(сообщения) в заказ
        DeliveryResponse deliveryResponse = httpSender.putToDelivery(URL_SERVICE_DELIVERIES + "/" + deliveryId + "/tracks/" + trackResponse.getId());
        logger.info("put Track to Delivery");

        return trackResponse;
    }


    @SneakyThrows
    @Override
    public List<Delivery> getDeliveriesOfUser(String token, Long page, Long size) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        Long userId = checkTokenResponse.getUserId();
        UserInfoResponse userInfoResponse = httpSender.getUser(URL_GET_USER_INFO, token);
        Boolean isCourier = userInfoResponse.getIsCourier();
        DeliveryListResponse deliveryListResponse;
        if (isCourier)
        {// взятие заказов текущего курьера
            deliveryListResponse = httpSender.getDeliveries(URL_SERVICE_DELIVERIES + "/courier/" + userId + "/" + page + "/" + size);
        }else
        {// взятие заказов текущего пользователя
            deliveryListResponse = httpSender.getDeliveries(URL_SERVICE_DELIVERIES + "/user/" + userId + "/" + page + "/" + size);
        }
        if (!deliveryListResponse.getCode())
            throw new Exception("get Deliveries");

        return deliveryListResponse.getDeliveries();

    }

    @SneakyThrows
    @Override
    public List<Delivery> getDeliveriesWithoutCourier(String token, Long page, Long size) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        Long userId = checkTokenResponse.getUserId();
        UserInfoResponse userInfoResponse = httpSender.getUser(URL_GET_USER_INFO, token);
        Boolean isCourier = userInfoResponse.getIsCourier();
        DeliveryListResponse deliveryListResponse;
        if (isCourier)
        {// взятие заказов текущего курьера
            deliveryListResponse = httpSender.getDeliveries(URL_SERVICE_DELIVERIES + "/courier/null/" + page + "/" + size);
        }else
        {
            return null;// ошибка прав доступа
        }
        if (!deliveryListResponse.getCode())
            throw new Exception("get Deliveries");

        return deliveryListResponse.getDeliveries();

    }

    @SneakyThrows
    @Override
    public void deleteTrack(String token, Long deliveryId, Long trackId) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        httpSender.deleteTrack(URL_SERVICE_TRACKS + "/" + trackId);
        httpSender.deleteFromDelivery(URL_SERVICE_DELIVERIES+ "/" + deliveryId + "/tracks/" + trackId);

        logger.info("delete Track from Delivery");
    }

    @SneakyThrows
    @Override
    public BillingResponse executeBilling(Long deliveryId, String token) {
        //  проверка токена, расчёт стоимости доставки
        Double cost = getCostOfDelivery(deliveryId, token);
        // оплата
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        BillingResponse billingResponse = httpSender.executeBilling(URL_SERVICE_BILLINGS + "/user/" + checkTokenResponse.getUserId() +"/billing/create", cost );

        if (!billingResponse.getCode())// если не прошла оплата
            return billingResponse;
        else {// если прошла оплата, то обновляем статус "оплачено' для заказа
            UpdateBillingDelivery updateBillingDelivery = new UpdateBillingDelivery();
            updateBillingDelivery.setPaid(true);
            updateBillingDelivery.setBillingId(billingResponse.getBilling().getId());
            httpSender.updateBillingDelivery(URL_SERVICE_DELIVERIES + "/" + deliveryId + "/billing", updateBillingDelivery);

            return billingResponse;
        }
    }
    @SneakyThrows
    @Override
    public BaseResponse createUserBilling(UserBillingInfo user, String token){
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        ///user/{userId}/create
        user.setUserId(checkTokenResponse.getUserId());
        BaseResponse baseResponse =  httpSender.createUserBilling(URL_SERVICE_BILLINGS + "/user/" + checkTokenResponse.getUserId() +"/create", user );
        return baseResponse;
    }
    @SneakyThrows
    @Override
    public BaseResponse returnBilling(Long deliveryId, String token){
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        BaseResponse baseResponse = new BaseResponse();

        DeliveryResponse deliveryResponse = httpSender.getDeliveryInfo(URL_SERVICE_DELIVERIES + "/"+ String.valueOf(deliveryId));
        if (deliveryResponse == null || !deliveryResponse.getCode())
        {
            baseResponse.setErrorCode(false);
            baseResponse.setErrorMessage("error");
            return baseResponse;
        }
        Delivery delivery = deliveryResponse.getDelivery();
        baseResponse = httpSender.returnBilling(URL_SERVICE_BILLINGS +"/user/" + checkTokenResponse.getUserId() + "/billing/" + delivery.getBilling());
        if (baseResponse == null || !baseResponse.getErrorCode())
        {
            baseResponse.setErrorCode(false);
            baseResponse.setErrorMessage("error");
            return baseResponse;
        }
        UpdateBillingDelivery updateBillingDelivery = new UpdateBillingDelivery();
        updateBillingDelivery.setPaid(false);
        updateBillingDelivery.setBillingId(null);
        httpSender.updateBillingDelivery(URL_SERVICE_DELIVERIES + "/" + deliveryId + "/billing", updateBillingDelivery);

        baseResponse.setErrorCode(true);
        baseResponse.setErrorMessage("");
        return baseResponse;
    }

    @SneakyThrows
    @Override
    public Double getCostOfDelivery(Long deliveryId, String token)
    {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);

        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        Long userId = checkTokenResponse.getUserId();

        DeliveryResponse deliveryResponse =  httpSender.getDeliveryInfo(URL_SERVICE_DELIVERIES + "/" + deliveryId);
        return  EstimationCostDelivery(deliveryResponse.getDelivery());
    }

    @SneakyThrows
    @Override
    public Double EstimationCostDelivery(Delivery delivery){
        Double Cost = 300.0;
        return Cost;
    }

    public void Autorization(String redirect_uri, String response_type, String client_id )
    {
        String url =
                "http://localhost:8080/oauth2/authorization" + "?" + "redirect_uri=" + redirect_uri +
                        "&response_type=" + response_type + "&client_id=" + client_id;
        httpSender.getAutorization(url);
    }

    public String getToken(String client_id, String client_secret, String redirect_uri, String code)
    {
        String url = "http://localhost:8080/oauth2/token" +
                "?client_id=" + client_id + "&client_secret=" + client_secret+"&redirect_uri="
        + redirect_uri + "&grant_type=authorization_code&" + "code=" + code;
        return httpSender.getToken(url);
    }

    public void getUser_with_auth()
    {
        String url = "http://localhost:8080/";
        httpSender.getUser_with_auth(url);
    }

}

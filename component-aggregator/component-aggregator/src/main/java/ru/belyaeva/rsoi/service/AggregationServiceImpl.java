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
    private final String URL_SERVICE_DELIVERIES = "http://localhost:8082/deliveryService/deliveries";
    private final String URL_SERVICE_TRACKS = "http://localhost:8083/trackService/tracks";
    private final String URL_SERVICE_BILLINGS = "http://localhost:8084/billingService";
    private final String CREATE_DELIVERY = "/create";


    @SneakyThrows
    @Override
    public Delivery createDelivery(String token, Delivery delivery) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
// добавить проверку на курьера
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
        Delivery delivery = deliveryResponse.getDelivery();

        DeliveryFull deliveryFull = new DeliveryFull();
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
           /* ProductListResponse productListResponse = httpSender.getProducts(URL_SERVICE_PRODUCTS + GET_PRODUCT, longIdListRequest);
            if (!productListResponse.getCode()) {
                throw new Exception(productListResponse.getMessage());
            }*/
            deliveryFull.setTracks(tracks);
        }

        BillingResponse billingResponse = httpSender.getBilling(URL_SERVICE_BILLINGS + "/user/" + checkTokenResponse.getUserId() + "/billing/" + delivery.getBilling());
        deliveryFull.setBilling(billingResponse);
        deliveryFull.setId(delivery.getId());
        deliveryFull.setUserId(delivery.getUserId());
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


        logger.info("get Delivery");
        return deliveryFull;
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
        TrackResponse trackResponse = httpSender.createTrack(URL_SERVICE_TRACKS + "/", track);
        if (trackResponse == null || !trackResponse.getCode()) {
            throw new Exception(trackResponse.getMessage());
        }

        // добавление трека(сообщения) в заказ
        DeliveryResponse deliveryResponse = httpSender.putToDelivery(URL_SERVICE_DELIVERIES + "/" + deliveryId + "/" + trackResponse.getId());
        if (deliveryResponse == null || !deliveryResponse.getCode()) {
            throw new Exception(deliveryResponse.getMessage());
        }
        logger.info("put Track to Delivery");

        return trackResponse;
    }

/*
    @SneakyThrows
    @Override
    public St ring getProducts(String token, Long page, Long size) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        String Response =
                httpSender.getProducts(URL_SERVICE_PRODUCTS + "/" + page + "/" + size);
        logger.info("get Product");
        return Response;
    }
*/
    @SneakyThrows
    @Override
    public List<Delivery> getDeliveries(String token, Long page, Long size) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        Long userId = checkTokenResponse.getUserId();
        DeliveryListResponse deliveryListResponse =
                httpSender.getDeliveries(URL_SERVICE_DELIVERIES + "/" + userId + "/" + page + "/" + size);

        if (!deliveryListResponse.getCode())
            throw new Exception("get Deliveries");
        return deliveryListResponse.getDeliveries();

    }

    /*
    @SneakyThrows
    @Override
    public Delivery updateOrder(Long orderId, String sellerName, String shopName, String token) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        UpdateDeliveryRequest updateOrderRequest = new UpdateDeliveryRequest();
        updateOrderRequest.setOrderId(orderId);
        updateOrderRequest.setShopName(shopName);
        updateOrderRequest.setSellerName(sellerName);

        DeliveryResponse deliveryResponse = httpSender.updateOrder(URL_SERVICE_ORDERS + "/" + orderId, updateOrderRequest);
        if (!deliveryResponse.getCode()) {
            throw new Exception(deliveryResponse.getMessage());
        }
        logger.info("update Delivery");
        return deliveryResponse.getOrder();
    }
    */
/*
    @SneakyThrows
    @Override
    public void putToOrder(Long orderId, Long goodId, String token) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        // проверка существования товара
        TrackResponse trackResponse = httpSender.getProduct(URL_SERVICE_PRODUCTS + "/" + goodId.toString());
        if (trackResponse == null || !trackResponse.getCode()) {
            throw new Exception("Товар №" + goodId.toString() + "не существует");
        }
        // его добавление
        httpSender.putToOrder(URL_SERVICE_ORDERS + "/" + orderId + "/" + goodId);
        logger.info("put Product to Delivery");
    }
*/
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

   /* @SneakyThrows
    @Override
    public DeliveryFull executeBilling(Long deliveryId, String token) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);

        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        // загрузка товаров
        DeliveryFull deliveryFull = getOrder(token, orderId);
        if (deliveryFull == null)
            throw new Exception("Ошибка billing. Невозможно загрузить заказ");
        if (deliveryFull.getPaid() == null || deliveryFull.getPaid())
            return deliveryFull;
        // подсчёт суммы чека
        List<TrackResponse> products = deliveryFull.getProducts();
        Double summary = 0.0;
        for (TrackResponse prod : products) {
            if (prod != null)
                summary += prod.getPrice();
        }
        // установка в БД
        BillingInfo billingInfo = new BillingInfo();
        billingInfo.setSummary(summary);
        billingInfo.setMeansPayment(meansPayment);
        DeliveryResponse deliveryResponse = httpSender.executeBillingOrder(URL_SERVICE_ORDERS + "/" + orderId + BILLING, billingInfo);
        if (!deliveryResponse.getCode()) {
            throw new Exception(deliveryResponse.getMessage());
        }
        Delivery delivery = deliveryResponse.getOrder();
        deliveryFull.setPaid(delivery.getPaid());
        deliveryFull.setBilling(delivery.getBilling());

        logger.info("create Billing");
        return deliveryFull;
    }*/

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

    public void getUser()
    {
        String url = "http://localhost:8080/";
        httpSender.getUser(url);
    }
}

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
    private final String URL_SERVICE_DELIVERIES = "http://localhost:8082/deliveryService/deiveries";
    private final String URL_SERVICE_TRACKS = "http://localhost:8083/trackService/tracks";

    private final String CREATE_DELIVERY = "/create";
   /* private final String GET_ORDERS = "/getOrders/";///getOrders/{userId}/{page}/{size}
    private final String GET_ORDER = "/getOrder/";///getOrder/{orderId}
    private final String UPDATE_ORDER = "/updateOrder";
    private final String PUT_TO_ORDER = "/putToOrder/";///putToOrder/{orderId}/{goodId}
    private final String DELETE_FROM_ORDER = "/deleteFromOrder/";///deleteFromOrder/{orderId}/{goodId}
    private final String EXECUTE_BILLING_ORDER = "/executeBilling/";///executeBilling/{orderId}
*/
    private final String BILLING = "/billing";


    private final String GET_PRODUCT = "/getProductFromOrder";


    @SneakyThrows
    @Override
    public Delivery createDelivery(String token, Delivery delivery) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }

        CreateDeliveryRequest createDeliveryRequest = new CreateDeliveryRequest();
        createDeliveryRequest.setUserId(checkTokenResponse.getUserId());
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
                TrackResponse trackResponse = httpSender.getProduct(URL_SERVICE_TRACKS + "/" + id_track.toString());
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
        deliveryFull.setId(delivery.getId());
        deliveryFull.setUserId(delivery.getUserId());
        deliveryFull.setPaid(delivery.getPaid());
        deliveryFull.setBilling(delivery.getBilling());
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
    public String getProducts(String token, Long page, Long size) {
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


    @SneakyThrows
    @Override
    public Delivery updateOrder(Long orderId, String sellerName, String shopName, String token) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        CheckTokenResponse checkTokenResponse = httpSender.checkToken(URL_CHECK_TOKEN, checkTokenRequest);
        if (checkTokenResponse == null || !checkTokenResponse.getValidToken()) {
            throw new IllegalAccessException("Token isn`t valid");
        }
        UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest();
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

        logger.info("dalete Track from Delivery");
    }

    @SneakyThrows
    @Override
    public DeliveryFull executeBilling(Long orderId, String meansPayment, String token) {
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

    public void getUser()
    {
        String url = "http://localhost:8080/";

         httpSender.getUser(url);
    }
}

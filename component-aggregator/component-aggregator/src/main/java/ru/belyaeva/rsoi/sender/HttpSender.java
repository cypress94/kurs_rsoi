package ru.belyaeva.rsoi.sender;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import ru.belyaeva.rsoi.model.*;

/**
 * Created by user on 22.11.2016.
 */
@Service
public class HttpSender {

    private static final RestTemplate restTemplate = new RestTemplate();

    public CheckTokenResponse checkToken(String url, CheckTokenRequest checkToken) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(url, checkToken.getToken(), CheckTokenResponse.class);
    }

    public DeliveryResponse createDelivery(String url, CreateDeliveryRequest request){
        return restTemplate.postForObject(url, request, DeliveryResponse.class);
    }

    public DeliveryResponse getDeliveryInfo(String url){
        return restTemplate.getForObject(url, DeliveryResponse.class);
    }

    public DeliveryListResponse getDeliveries(String url){
        return restTemplate.getForObject(url, DeliveryListResponse.class);
    }

    public String getProducts(String url){
        return restTemplate.getForObject(url, String.class);
    }


    public DeliveryResponse updateDelivery(String url, UpdateDeliveryRequest request){
        return restTemplate.postForObject(url, request, DeliveryResponse.class);
    }

    public DeliveryResponse updateBillingDelivery(String url, UpdateBillingDelivery updateBillingDelivery){
        return restTemplate.postForObject(url,updateBillingDelivery, DeliveryResponse.class);
    }


    public DeliveryResponse putToDelivery(String url){
        RequestNull requestNull = new RequestNull();
       // restTemplate.put(url, requestNull);
        return restTemplate.postForObject(url, requestNull, DeliveryResponse.class);

    }

    public void deleteFromDelivery(String url){
        restTemplate.delete(url);
    }
    public void deleteTrack(String url){
        restTemplate.delete(url);
    }

    public BillingResponse executeBilling(String url, Double cost){
        return restTemplate.postForObject(url, cost, BillingResponse.class);
    }

    public BillingResponse getBilling(String url){

        return restTemplate.getForObject(url, BillingResponse.class);
    }

    public BaseResponse returnBilling(String url){
        return restTemplate.postForObject(url, null, BaseResponse.class);
    }
    public void deleteBilling(String url){
        restTemplate.delete(url);
    }
    public void deleteDelivery(String url){
        restTemplate.delete(url);
    }
    public BaseResponse createUserBilling(String url, UserBillingInfo userBillingInfo){
        return restTemplate.postForObject(url, userBillingInfo, BaseResponse.class);
    }
    public void getAutorization(String url)
    {
        restTemplate.getForObject(url, ModelAndView.class);
    }

    public TrackResponse createTrack(String url, Track track){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(url, track, TrackResponse.class);
    }

    public String getToken(String url)
    {
        return restTemplate.postForObject(url, null, String.class);
    }

    public UserInfoResponse getUser(String url, String token)
    {
       return restTemplate.postForObject(url, token, UserInfoResponse.class);
    }
    public void getUser_with_auth(String url)
    {
        restTemplate.getForObject(url, UserInfoResponse.class);
    }
/*
    public BillingResponse createBilling(String url){
        return restTemplate.getForObject(url, BillingResponse.class);
    }

    public BillingResponse executeBilling(String url, ExecuteBillingRequest request){
        return restTemplate.getForObject(url, BillingResponse.class, request);
    }

    public BillingResponse getBilling(String url){
        return restTemplate.getForObject(url, BillingResponse.class);
    }
*/
    public ProductListResponse getProducts(String url, LongIdListRequest request){
        return restTemplate.postForObject(url, request, ProductListResponse.class);
    }

    public TrackResponse getTrack(String url){
        return restTemplate.getForObject(url, TrackResponse.class);
    }

}

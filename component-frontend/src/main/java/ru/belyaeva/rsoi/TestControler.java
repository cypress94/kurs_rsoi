package ru.belyaeva.rsoi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.belyaeva.rsoi.model.*;


import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.belyaeva.rsoi.factories.ConnectionFactory;
import ru.belyaeva.rsoi.factories.OAuthBeansFactory;
import ru.belyaeva.rsoi.WorkWithHTTP;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@EnableAutoConfiguration
public class TestControler {
    private String accessToken;
    private static Logger logger = LoggerFactory.getLogger(TestControler.class);
    private static WorkWithHTTP http = new WorkWithHTTP();


    @GetMapping("/")
    String index(Model model) {
        model.addAttribute("now", LocalDateTime.now());
        return "index";
    }

    @GetMapping("/badlogin")
    @ResponseBody
    public ModelAndView badlogin(Model model) {
        return new ModelAndView("badlogin");
    }

    @GetMapping("/getIndex")
    @ResponseBody
    public ModelAndView getIndex(Model model) {
        model.addAttribute("now", LocalDateTime.now());
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/auth", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView getMyAuth() {
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        String urlString =
                oAuth2Bean.getAuthEndpoint() + "?" + "redirect_uri=" + oAuth2Bean.getRedirectEndpoint() +
                        "&response_type=" +
                        oAuth2Bean.getResponseType() +
                        "&client_id=" + oAuth2Bean.getClientId();
        logger.info("Request to remote system to get autorithation code: {}", urlString);
        return new ModelAndView("redirect:" + urlString);

    }

    @RequestMapping(value = "/profilemyauth", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView getProfileMyAuth( HttpServletRequest request)
            throws ServletException, IOException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getTokenEndpoint(), "post");


        String code = request.getParameter("code");

        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append("client_id=").append(oAuth2Bean.getClientId()).append("&client_secret=")
                .append(oAuth2Bean.getClientSecret()).append("&redirect_uri=")
                .append(oAuth2Bean.getRedirectEndpoint()).append("&grant_type=authorization_code&")
                .append("code=").append(code);
        httpURLConnection.setDoOutput(true);
        logger.info("Request to remote system to get access token: {}", urlStringBuilder.toString());
        String response = http.writeAndReadFromHttpConnection(httpURLConnection, urlStringBuilder.toString());// получение токена

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> answerMap = new HashMap<String, String>();
        try {
            answerMap = objectMapper.readValue(response, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            logger.error("ERROR: when casting json answer to map");
        }
        accessToken = answerMap.get("access_token");
        //-------------------
        httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "get");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to get info about user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);

        logger.info("User info has taken: {}", profileResponse);

        JsonNode node = objectMapper.readTree(profileResponse);
        String errorCode = objectMapper.convertValue(node.get("error_code"), String.class);
        if (errorCode == null || errorCode.isEmpty()) {
            String name = objectMapper.convertValue(node.get("display_name"), String.class);
            String email = objectMapper.convertValue(node.get("email"), String.class);

            ModelAndView nmav = new ModelAndView("profilePage");
            nmav.addObject("name", name);
            nmav.addObject("email", email);
            return nmav;
        }
        String errorText = objectMapper.convertValue(node.get("error_message"), String.class);
        ModelAndView nmav = new ModelAndView("serverError");
        nmav.addObject("errorcode", errorCode);
        nmav.addObject("errormessage", errorText);
        return nmav;

    }

    // Начальная регистрация пользователя (выдача формы заполнения инфы)
    @RequestMapping(value = "/delivery/create", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView DeliveryCreate_step1()
    {
        Delivery delivery = new Delivery();

        ModelAndView nmav = new ModelAndView("createDelivery");
        nmav.addObject("delivery", delivery);
        return nmav;
    }
    @RequestMapping(value = "/delivery/create/full", produces = "application/json", method = RequestMethod.POST)
    @CrossOrigin
    @ResponseBody
    public ModelAndView DeliveryCreate_step2(
            @ModelAttribute Delivery delivery, Model model)
            throws ServletException, IOException{

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/delivery?" +
                "name="+ delivery.getName().replace(" ", "_") +"&" +
                "weight=" + delivery.getWeight() + "&" +
                "width=" + delivery.getWidth() + "&" +
                "height=" + delivery.getHeight() + "&" +
                "deep=" + delivery.getDeep() + "&" +
                "timedelivery=" + delivery.getTimeDelivery().replace(" ", "_") + "&" +
                "address_start=" + delivery.getAddressStart().replace(" ", "_") + "&" +
                "address_finish=" + delivery.getAddressFinish().replace(" ", "_") + "&" +
                "description=" + delivery.getDescription().replace(" ", "_")
        );

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "POST");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        StringBuilder urlStringBuilder = new StringBuilder();
        String response = http.writeAndReadFromHttpConnection(httpURLConnection, urlStringBuilder.toString());
        if (profileResponse == null || profileResponse == "") {
            logger.info("Delivery create: access privilege error");
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage");
            return nmav;
        }
        logger.info("Delivery has create: {}", profileResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree( profileResponse );
        delivery = objectMapper.convertValue(node, Delivery.class);

        ModelAndView nmav = new ModelAndView("deliveryFullPage");
        nmav.addObject("delivery", delivery);
        nmav.addObject("cost", getCost(delivery.getId()));
        nmav.addObject("haveBilling", false);
        nmav.addObject("haveTracks", false);
        Boolean isCourier = getIsCourier();
        nmav.addObject("isCourier", isCourier);
        nmav.addObject("userId", getUserId());
        return nmav;
    }
    @RequestMapping(value = "/delivery/{deliveryId}/track/create", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView createTrack_step1(@PathVariable("deliveryId") Long deliveryId)
            throws ServletException, IOException {
        Track track = new Track();
        ModelAndView nmav = new ModelAndView("createTrack");
        nmav.addObject("track", track);
        nmav.addObject("deliveryId", deliveryId);
        return nmav;
    }

    @RequestMapping(value = "/delivery/{deliveryId}/track/create/full", produces = "application/json", method = RequestMethod.POST)
    @CrossOrigin
    @ResponseBody
    public ModelAndView createTrack_step2(
    @PathVariable("deliveryId") Long deliveryId,
    @ModelAttribute Track track, Model model)
            throws ServletException, IOException{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/delivery/" + deliveryId +"/track?" +
                                "track_message=" + track.getTrackMessage().replace(" ", "_"));

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "post");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        if (profileResponse == null || profileResponse == "") {
            logger.info("Track create: Server error");
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage", "ошибка сервера");
            return nmav;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree( profileResponse );
        TrackResponse trackResponse = objectMapper.convertValue(node, TrackResponse.class);
        if (!trackResponse.getCode())
        {
            logger.info("Track create: access privilege error");
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage", "ошибка прав доступа");
            return nmav;
        }
        ModelAndView nmav = new ModelAndView("trackPage");
        nmav.addObject("track", trackResponse);
        return nmav;
    }

    public Double getCost(Long deliveryId)
            throws ServletException, IOException{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/delivery/" + deliveryId + "/cost");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "get");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree( profileResponse );
        return objectMapper.convertValue(node, Double.class);
    }

    public Long getUserId()
            throws ServletException, IOException{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/user/id");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "get");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree( profileResponse );
        return objectMapper.convertValue(node, Long.class);
    }

    public Boolean getIsCourier()
            throws ServletException, IOException{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/user/type");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "get");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree( profileResponse );
        return objectMapper.convertValue(node, Boolean.class);
    }

    @RequestMapping(value = "/delivery/{deliveryId}", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView getFullDelivery(
            @PathVariable("deliveryId") Long deliveryId)
            throws ServletException, IOException{

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/delivery/" + deliveryId);

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "get");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        if (profileResponse == null || profileResponse == "") {
            logger.info("Track create: Server error");
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage", "ошибка сервера");
            return nmav;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree( profileResponse );
        DeliveryFull deliveryFull = objectMapper.convertValue(node, DeliveryFull.class);
        if (deliveryFull == null )
        {
            logger.info("get delivery: error of server");
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage", "ошибка сервера");
            return nmav;
        }
        if (!deliveryFull.getCode())
        {// ошибка прав доступа
            logger.info("get delivery: error of server");
            ModelAndView nmav = new ModelAndView("serverError");
           // nmav.addObject("errorcode", -1);
            nmav.addObject("errormessage", "ошибка прав доступа");

            return nmav;
        }
        ModelAndView nmav = new ModelAndView("deliveryFullPage");
        nmav.addObject("delivery", deliveryFull);
        nmav.addObject("haveMessage", false);
        nmav.addObject("Message", "");
        Boolean isCourier = getIsCourier();
        nmav.addObject("isCourier", isCourier);
        nmav.addObject("userId", getUserId());

        if ( !deliveryFull.getPaid() )
            nmav.addObject("haveBilling", false);
        else
            nmav.addObject("haveBilling", true);
        if (deliveryFull.getTracks() != null && !deliveryFull.getTracks().isEmpty())
            nmav.addObject("haveTracks", true);
        else
            nmav.addObject("haveTracks", false);
        nmav.addObject("cost", getCost(deliveryId));

        return nmav;
    }

    @RequestMapping(value = "/deliveries/page/{page}/size/{size}", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView getDeliveries(
            @PathVariable("page") Long page,
            @PathVariable("size") Long size )
            throws ServletException, IOException {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/deliveries?page=1&size=3");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "get");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        logger.info("get owner deliveries : {}", profileResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree( profileResponse );
        List<Delivery> deliveries = objectMapper.convertValue(node, List.class);
        if (deliveries == null || deliveries.size() == 0)
        {
            logger.info("Track create: access privilege error");
            ModelAndView nmav = new ModelAndView("deliveriesPage");
            nmav.addObject("haveDeliveries", false);
            Boolean isCourier = getIsCourier();
            nmav.addObject("isCourier", isCourier);
            return nmav;
        }
        ModelAndView nmav = new ModelAndView("deliveriesPage");
        nmav.addObject("haveDeliveries", true);
        nmav.addObject("deliveries", deliveries);
        Boolean isCourier = getIsCourier();
        nmav.addObject("isCourier", isCourier);

        return nmav;

    }
    @RequestMapping(value = "/deliveries/courier/null/page/{page}/size/{size}", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView getDeliveriesWithoutCourier(
            @PathVariable("page") Long page,
            @PathVariable("size") Long size )
            throws ServletException, IOException {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/deliveries/courier/null?page=1&size=3");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "get");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        logger.info("get owner deliveries : {}", profileResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree( profileResponse );
        List<Delivery> deliveries = objectMapper.convertValue(node, List.class);
        if (deliveries == null || deliveries.size() == 0)
        {
            ModelAndView nmav = new ModelAndView("deliveriesWithoutCPage");
            nmav.addObject("haveDeliveries", false);

            return nmav;
        }
        ModelAndView nmav = new ModelAndView("deliveriesWithoutCPage");
        nmav.addObject("haveDeliveries", true);
        nmav.addObject("deliveries", deliveries);
        return nmav;

    }

    @RequestMapping(value = "/delivery/{deliveryId}/reserved", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView reservedDeliveryByCourier(
            @PathVariable("deliveryId") Long deliveryId)
            throws ServletException, IOException {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/delivery/"+ deliveryId);

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "post");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        logger.info("get owner deliveries : {}", profileResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree( profileResponse );
        DeliveryFull delivery = objectMapper.convertValue(node, DeliveryFull.class);
        if ( delivery == null )
        {
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage", "ошибка прав доступа");

            return nmav;
        }

        ModelAndView nmav = new ModelAndView("deliveryFullPage");
        nmav.addObject("haveMessage", true);
        nmav.addObject("Message", "Заказ успешно зарезервирован");
        nmav.addObject("isCourier", true);
        nmav.addObject("userId", getUserId());

        nmav.addObject("delivery", delivery);
        if ( !delivery.getPaid() )
            nmav.addObject("haveBilling", false);
        else
            nmav.addObject("haveBilling", true);
        if (delivery.getTracks() != null && !delivery.getTracks().isEmpty())
            nmav.addObject("haveTracks", true);
        else
            nmav.addObject("haveTracks", false);
        nmav.addObject("cost", getCost(deliveryId));
        return nmav;
    }

    // Начальная регистрация пользователя (выдача формы заполнения инфы)
    @RequestMapping(value = "/billing/user/create", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView createUserBilling()
    {
        UserBillingInfo userBillingInfo = new UserBillingInfo();

        ModelAndView nmav = new ModelAndView("createUserBilling");
        nmav.addObject("user", userBillingInfo);
        return nmav;
    }
    // Регистрация пользователя (окончательная)
    @RequestMapping(value = "delivery/{id}/user/registration", method = RequestMethod.POST)
    public ModelAndView createUserBilling_step2(
            @PathVariable("id") Long deliveryId,
            @ModelAttribute UserBillingInfo user, Model model)
            throws ServletException, IOException
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/billing/user/create?" +
                "means_payment=" + user.getMeansPayment() + "&" +
                "card_number="   + user.getCardNumber()   + "&" +
                "cvv="           + user.getCVV()          + "&" +
                "summary="       + user.getSummary());

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "post");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        if (profileResponse == null || profileResponse == "") {
            logger.info("Track create: Server error");
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage", "ошибка сервера");
           // return nmav;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree( profileResponse );
        BaseResponse baseResponse = objectMapper.convertValue(node, BaseResponse.class);
        if (baseResponse == null)
        {
            logger.info("get delivery: error of server");
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage", "ошибка сервера");
            return nmav;
        }
        return executeBilling(deliveryId);
    }

    @RequestMapping(value = "/delivery/{deliveryId}/billing", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView executeBilling(
            @PathVariable("deliveryId") Long deliveryId)
            throws ServletException, IOException{

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/delivery/" + deliveryId + "/billing");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "post");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        if (profileResponse == null || profileResponse == "") {
            logger.info("Track create: Server error");
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage", "ошибка сервера");
            return nmav;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree( profileResponse );
        BillingResponse billingResponse = objectMapper.convertValue(node, BillingResponse.class);
        if (billingResponse == null)
        {
            logger.info("get delivery: error of server");
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage", "ошибка сервера");
            return nmav;
        }
        String mes = billingResponse.getMessage();

        if (!billingResponse.getCode() && mes.equals("error user billing"))
        {// пользователь не зарегестрирован в системе оплаты
            ModelAndView nmav = createUserBilling();
            return nmav;
        }

        if (!billingResponse.getCode() && mes.equals("error summary"))
        {
                ModelAndView nmav = new ModelAndView("errorBillingPage");
                return nmav;
        }

        ModelAndView nmav = new ModelAndView("billingPage");
        nmav.addObject("isExecute", true);
        nmav.addObject("billingTime", billingResponse.getBilling().getBillingTime());

        nmav.addObject("cost", getCost(deliveryId));
        return nmav;
    }
    @RequestMapping(value = "/delivery/{deliveryId}/billing/return", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView returnBilling(
            @PathVariable("deliveryId") Long deliveryId)
            throws ServletException, IOException{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/delivery/" + deliveryId + "/billing/return");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "post");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        if (profileResponse == null || profileResponse == "") {
            logger.info("Track create: Server error");
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage", "ошибка сервера");
            return nmav;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree( profileResponse );
        BaseResponse baseResponse = objectMapper.convertValue(node, BaseResponse.class);
        if (baseResponse == null || !baseResponse.getErrorCode() ){
            logger.info("get delivery: error of server");
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage", "ошибка сервера");
            return nmav;
        }
        ModelAndView nmav = new ModelAndView("billingPage");
        nmav.addObject("isExecute", false);
        return nmav;
    }

    @RequestMapping(value = "/delivery/{deliveryId}/del", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView deleteDelivery(
            @PathVariable("deliveryId") Long deliveryId)
            throws ServletException, IOException{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/delivery/" + deliveryId);

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "delete");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        if (profileResponse == null || profileResponse == "") {
            logger.info("Track create: Server error");
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage", "ошибка сервера");
            return nmav;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree( profileResponse );
        BaseResponse baseResponse = objectMapper.convertValue(node, BaseResponse.class);
        if (baseResponse == null || !baseResponse.getErrorCode() ){
            logger.info("get delivery: error of server");
            ModelAndView nmav = new ModelAndView("serverError");
            nmav.addObject("errormessage", "ошибка сервера");
            return nmav;
        }
        Long page = new Long (1);
        Long size = new Long (3);
        return getDeliveries(page, size);
    }


}




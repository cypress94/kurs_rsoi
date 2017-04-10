package ru.belyaeva.rsoi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.belyaeva.rsoi.factories.ConnectionFactory;
import ru.belyaeva.rsoi.model.OAuth2Bean;
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

    // laba2
    @RequestMapping(value = "/getMyAuth", produces = "application/json", method = RequestMethod.GET)
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

    // laba2
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


    @RequestMapping(value = "/setOrder", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView setOrder() {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/order");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "post");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        StringBuilder urlStringBuilder = new StringBuilder();
        String response = http.writeAndReadFromHttpConnection(httpURLConnection, urlStringBuilder.toString());
        logger.info("Order has create: {}", profileResponse);

        ModelAndView nmav = new ModelAndView("response");
        nmav.addObject("response", profileResponse);
        return nmav;

    }

    @RequestMapping(value = "/setProduct1toOrder1", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView setProduct() {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/order/1/product/1");
    //?product_name=dress&price=2000
        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "put");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);

        logger.info("Product 1 in order 1 is created: {}", profileResponse);

        ModelAndView nmav = new ModelAndView("response");
        nmav.addObject("response", profileResponse);
        return nmav;

    }

    @RequestMapping(value = "/updateOrder1", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView updateOrder1() {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/order/1?seller_name=KatyZARA&shop_name=AliExpress");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "POST");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);

        logger.info("Product 1 in order 1 is created: {}", profileResponse);

        ModelAndView nmav = new ModelAndView("response");
        nmav.addObject("response", profileResponse);
        return nmav;

    }

    @RequestMapping(value = "/deleteProduct1fromOrder1", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView deleteProduct1() {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/order/1/product/1");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "delete");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);

        logger.info("Product 1 deleted from order 1 : {}", profileResponse);

        ModelAndView nmav = new ModelAndView("response");
        nmav.addObject("response", profileResponse);
        return nmav;

    }

    @RequestMapping(value = "/getOrders", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView getOrders() {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/orders?page=1&size=3");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "get");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        logger.info("get all Orders: {}", profileResponse);

        ModelAndView nmav = new ModelAndView("response");
        nmav.addObject("response", profileResponse);
        return nmav;

    }
    @RequestMapping(value = "/getOrder1", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView getOrder1() {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/order/1");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "get");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        logger.info("get Order #1: {}", profileResponse);

        ModelAndView nmav = new ModelAndView("response");
        nmav.addObject("response", profileResponse);
        return nmav;

    }
    @RequestMapping(value = "/getProduct1", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView getProduct1() {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/product/1");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "get");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        logger.info("get all Orders: {}", profileResponse);

        ModelAndView nmav = new ModelAndView("response");
        nmav.addObject("response", profileResponse);
        return nmav;

    }

    @RequestMapping(value = "/getProducts", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView getProducts() {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/products?page=1&size=3");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "get");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        logger.info("get all Orders: {}", profileResponse);

        ModelAndView nmav = new ModelAndView("response");
        nmav.addObject("response", profileResponse);
        return nmav;

    }
    @RequestMapping(value = "/setBilling", produces = "application/json", method = RequestMethod.GET)
    @CrossOrigin
    @ResponseBody
    public ModelAndView setBilling() {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        OAuthBeansFactory oAuthBeansFactory = new OAuthBeansFactory();
        OAuth2Bean oAuth2Bean = oAuthBeansFactory.createOAuth2BeanMine();
        oAuth2Bean.setProfileEndpoint("http://localhost:8090/order/1/billing?means_payment=Visa_Gold");

        HttpURLConnection httpURLConnection =
                connectionFactory.createHttpConnection(oAuth2Bean.getProfileEndpoint(), "post");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        logger.info("Request to remote system to set order user with access token: {}", accessToken);

        String profileResponse = http.readFromHttpConnection(httpURLConnection);
        logger.info("get Billing: {}", profileResponse);

        ModelAndView nmav = new ModelAndView("response");
        nmav.addObject("response", profileResponse);
        return nmav;

    }
}




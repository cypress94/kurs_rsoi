package ru.belyaeva.rsoi.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.belyaeva.rsoi.sender.HttpSender;
import ru.belyaeva.rsoi.service.AuthServiceImpl;
import ru.belyaeva.rsoi.service.LoginServiceImpl;
import ru.belyaeva.rsoi.web.model.CheckTokenRequest;
import ru.belyaeva.rsoi.web.model.CheckTokenResponse;
import ru.belyaeva.rsoi.web.model.TokenResponse;
import ru.belyaeva.rsoi.web.model.User;

import java.util.Map;

/**
 * Created by user on 21.11.2016.
 */
@RestController
@RequestMapping("/oauth2")
public class AuthController {

    @Autowired
    LoginServiceImpl loginServiceImpl;

    @Autowired
    AuthServiceImpl authServiceImpl;

    @Autowired
    HttpSender httpSender;

    @RequestMapping(value = "/authorization", method = RequestMethod.GET)
    public ModelAndView getAuth(
            @RequestParam(value = "client_id") String clientId,
            @RequestParam(value = "redirect_uri") String redirectUri,
            @RequestParam(value = "response_type") String responseType,
            @RequestHeader(value = "Authorization", required = false) String credentials) {

        if (!"code".equals(responseType)) {
            throw new IllegalArgumentException("Unknown value in request param \"response_type\"");
        }

        ModelAndView newModelAndView = new ModelAndView("acceptPage");
        User user = new User();
        //#TODO validation parameters and db params
        String userName = authServiceImpl.getUserName(clientId);
        Long requestId = authServiceImpl.createRequest(clientId, redirectUri);

        user.setRequestId(requestId);
        newModelAndView.addObject("user", user);
        newModelAndView.addObject("company", userName);

        return newModelAndView;
    }

    @RequestMapping(value = "/acceptAuth", method = RequestMethod.POST)
    public ModelAndView acceptAuth(@ModelAttribute User user,
            @RequestHeader(value = "Authorization", required = false) String credentials) {

        if (!loginServiceImpl.checkLogin(user)) {
            ModelAndView nmav = new ModelAndView("index");
            nmav.addObject("loginError", true);
            return nmav;
        }
        Long requestId = user.getRequestId();

        Map<String, String> mapResult = authServiceImpl.generateCode(user.getUsername(), requestId);
        String redirectUri = mapResult.get("redirectUri");

        String newUrl = "redirect:" + redirectUri + "?code=" + mapResult.get("code");
        return new ModelAndView(newUrl);
    }

    @RequestMapping(value = "/token",
            params = {"client_id", "client_secret", "redirect_uri", "grant_type", "code"},
            method = RequestMethod.POST)
    @ResponseBody
    public TokenResponse getToken(
            @RequestParam(value = "client_id") String clientId,
            @RequestParam(value = "client_secret") String clientSecret,
            @RequestParam(value = "redirect_uri") String redirectUri,
            @RequestParam(value = "grant_type") String grantType,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "refresh_token", required = false) String refreshToken,
            @RequestHeader(value = "Authorization", required = false) String credentials) {

        if (!"authorization_code".equals(grantType)) {
            throw new IllegalArgumentException("Unknown value in request param \"grant_type\"");
        }
        if (!authServiceImpl.checkUserInfo(clientId, clientSecret)) {
            throw new IllegalArgumentException("client_id/client_secret are incorrect");
        }

        if (refreshToken == null || refreshToken.isEmpty()) {
            if (code == null || code.isEmpty()) {
                throw new IllegalArgumentException("code can`t be null or empty");
            }
            return authServiceImpl.generateToken(code);
        } else {
            return authServiceImpl.refreshToken(refreshToken);
        }


    }

    @RequestMapping(value = "/checkToken", method = RequestMethod.POST)
   // @ResponseBody
    public CheckTokenResponse checkToken(@RequestBody String token) {
        CheckTokenRequest checkTokenRequest = new CheckTokenRequest(token);
        if (checkTokenRequest == null || checkTokenRequest.getToken() == null || checkTokenRequest.getToken().isEmpty()) {
            throw new IllegalArgumentException("token is not present in request");
        }

        return authServiceImpl.checkToken(checkTokenRequest);
    }

}

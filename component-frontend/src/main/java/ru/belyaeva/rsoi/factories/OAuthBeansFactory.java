package ru.belyaeva.rsoi.factories;


import ru.belyaeva.rsoi.model.OAuth2Bean;

public class OAuthBeansFactory {

    public OAuth2Bean createOAuth2BeanMine(){
        OAuth2Bean oAuth2Bean = new OAuth2Bean();
        oAuth2Bean.setAccessToken("");
        oAuth2Bean.setAccessType("online");
        oAuth2Bean.setAuthEndpoint("http://localhost:8080/oauth2/authorization");
        oAuth2Bean.setTokenEndpoint("http://localhost:8080/oauth2/token");
        oAuth2Bean.setClientId("1002185765"); // 1002185765
        oAuth2Bean.setClientSecret("sec1002185765");
        oAuth2Bean.setRedirectEndpoint("http://localhost:8081/profilemyauth");
        oAuth2Bean.setScope("profile");
        oAuth2Bean.setResponseType("code");
        oAuth2Bean.setProfileEndpoint("http://localhost:8080/me");
        return oAuth2Bean;
    }
}
// user 1191773281
// courier 1002185765

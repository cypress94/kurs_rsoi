package ru.belyaeva.rsoi.factories;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by user on 20.10.2016.
 */
public class ConnectionFactory {
    public HttpsURLConnection createHttpsConnection(String urlStringWithParams, String method   ) {
        URL url = null;
        HttpsURLConnection httpsURLConnection = null;
        try {
            url = new URL(urlStringWithParams);

            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            /*httpsURLConnection.setRequestProperty("scope",oAuth2Bean.getScope());
            httpsURLConnection.setRequestProperty("state","security_token");
            httpsURLConnection.setRequestProperty("redirect_uri",oAuth2Bean.getRedirectEndpoint());
            httpsURLConnection.setRequestProperty("response_type",oAuth2Bean.getResponseType());
            httpsURLConnection.setRequestProperty("cliend_id",oAuth2Bean.getClientId());
            */
            httpsURLConnection.setRequestMethod(method.toUpperCase());
        } catch (MalformedURLException e) {
            System.out.println("log: ERROR was occurred when connection was tried to create: " + e);
        } catch (IOException e) {
            System.out.println("log: ERROR was occerred when connection was tried to open " + e);
        }
        return httpsURLConnection;
    }

    public HttpURLConnection createHttpConnection(String urlStringWithParams, String method   ) {
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        try {
            url = new URL(urlStringWithParams);

            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod(method.toUpperCase());
        } catch (MalformedURLException e) {
            System.out.println("log: ERROR was occurred when connection was tried to create: " + e);
        } catch (IOException e) {
            System.out.println("log: ERROR was occerred when connection was tried to open " + e);
        }
        return httpURLConnection;
    }
}

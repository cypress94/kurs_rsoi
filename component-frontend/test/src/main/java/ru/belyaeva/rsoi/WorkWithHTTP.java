package ru.belyaeva.rsoi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;



/**
 * Created by user on 16.12.2016.
 */
public class WorkWithHTTP {
    private static Logger logger = LoggerFactory.getLogger(WorkWithHTTP.class);

    public String writeAndReadFromHttpConnection(HttpURLConnection httpURLConnection, String request) {
        String response = "vse o4en` ploxo";
        BufferedReader bufferedReader = null;
        try {
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(request.toString());
            wr.flush();
            wr.close();

            bufferedReader = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuilder builderResponse = new StringBuilder();

            while ((inputLine = bufferedReader.readLine()) != null) {
                builderResponse.append(inputLine);
            }
            response = builderResponse.toString();
        } catch (IOException e) {
            logger.error("ERROR: when send request and try to get response");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return response;
    }

    public String readFromHttpConnection(HttpURLConnection httpURLConnection) {
        BufferedReader bufferedReader = null;
        String response = "";
        try {


            bufferedReader = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuilder builderResponse = new StringBuilder();

            while ((inputLine = bufferedReader.readLine()) != null) {
                builderResponse.append(inputLine);
            }
            response = builderResponse.toString();
        } catch (IOException e) {
            logger.error("ERROR: when send request");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return response;
    }

    public String writeAndReadFromHttpsConnection(HttpsURLConnection httpsURLConnection, String request) {
        String response = "vse o4en` ploxo";
        BufferedReader bufferedReader = null;
        try {
            DataOutputStream wr = new DataOutputStream(httpsURLConnection.getOutputStream());
            wr.writeBytes(request.toString());
            wr.flush();
            wr.close();

            bufferedReader = new BufferedReader(
                    new InputStreamReader(httpsURLConnection.getInputStream()));
            String inputLine;
            StringBuilder builderResponse = new StringBuilder();

            while ((inputLine = bufferedReader.readLine()) != null) {
                builderResponse.append(inputLine);
            }
            response = builderResponse.toString();
        } catch (IOException e) {
            logger.error("ERROR: when send request and try to get response");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return response;
    }

    public String readFromHttpsConnection(HttpsURLConnection httpsURLConnection) {
        BufferedReader bufferedReader = null;
        String response = "";
        try {


            bufferedReader = new BufferedReader(
                    new InputStreamReader(httpsURLConnection.getInputStream()));
            String inputLine;
            StringBuilder builderResponse = new StringBuilder();

            while ((inputLine = bufferedReader.readLine()) != null) {
                builderResponse.append(inputLine);
            }
            response = builderResponse.toString();
        } catch (IOException e) {
            logger.error("ERROR: when send request");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return response;
    }

}

package ru.belyaeva.rsoi.sender;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by user on 22.11.2016.
 */
@Service
public class HttpSender {

    private static final RestTemplate restTemplate = new RestTemplate();

    public void send(String url) {
        restTemplate.getForObject(url, String.class);
    }
}

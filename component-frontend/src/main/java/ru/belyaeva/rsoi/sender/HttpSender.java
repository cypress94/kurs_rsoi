package ru.belyaeva.rsoi.sender;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import ru.belyaeva.rsoi.model.DeliveryResponse;

/**
 * Created by user on 22.11.2016.
 */
@Service
public class HttpSender {

    private static final RestTemplate restTemplate = new RestTemplate();



}

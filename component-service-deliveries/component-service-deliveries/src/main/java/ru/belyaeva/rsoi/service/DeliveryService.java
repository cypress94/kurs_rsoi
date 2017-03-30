package ru.belyaeva.rsoi.service;

import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.model.*;

/**
 * Created by user on 22.11.2016.
 */
@Service
public interface DeliveryService {

    DeliveryResponse createDelivery(CreateDeliveryRequest deliveryRequest);

    DeliveryListResponse getDeliveries(Long userId, Long page, Long size);

    DeliveryResponse getDelivery(Long deliveryId);

    DeliveryResponse updateDelivery (UpdateDeliveryRequest delivery);

    DeliveryResponse putToDelivery(Long deliveryId, Long trackId);

    DeliveryResponse deleteFromDelivery(Long deliveryId, Long trackId);

    DeliveryResponse executeBilling(Long deliveryId, BillingInfo billingInfo);

}

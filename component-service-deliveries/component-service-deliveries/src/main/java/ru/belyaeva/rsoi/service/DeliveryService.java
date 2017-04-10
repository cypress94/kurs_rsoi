package ru.belyaeva.rsoi.service;

import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.domain.DeliveryEntity;
import ru.belyaeva.rsoi.model.*;

/**
 * Created by user on 22.11.2016.
 */
@Service
public interface DeliveryService {

    DeliveryResponse createDelivery(CreateDeliveryRequest deliveryRequest);

    DeliveryListResponse getDeliveries(Long userId, Long page, Long size);

    DeliveryResponse getDelivery(Long deliveryId);

    Delivery getDelivery(DeliveryEntity deliveryEntity);

    DeliveryResponse updateDelivery (UpdateDeliveryRequest delivery);

    DeliveryResponse putToDelivery(Long deliveryId, Long trackId);

    DeliveryResponse deleteFromDelivery(Long deliveryId, Long trackId);

    DeliveryResponse updateBillingDelivery(Long deliveryId, UpdateBillingDelivery updateBillingDelivery);

   // DeliveryResponse executeBilling(Long deliveryId, BillingInfo billingInfo);

}

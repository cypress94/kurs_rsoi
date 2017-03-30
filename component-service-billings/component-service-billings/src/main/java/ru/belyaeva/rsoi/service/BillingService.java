package ru.belyaeva.rsoi.service;

import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.model.*;

/**
 * Created by user on 22.11.2016.
 */
@Service
public interface BillingService {

    BillingResponse executeBilling(Billing billingInfo);

    BaseResponse createUserBilling(UserBillingInfo userBillingInfo);

}

package ru.belyaeva.rsoi.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.domain.BillingEntity;
import ru.belyaeva.rsoi.domain.UserEntity;
import ru.belyaeva.rsoi.model.BaseResponse;
import ru.belyaeva.rsoi.model.Billing;
import ru.belyaeva.rsoi.model.BillingResponse;
import ru.belyaeva.rsoi.model.UserBillingInfo;
import ru.belyaeva.rsoi.repository.BillingRepository;
import ru.belyaeva.rsoi.repository.UserRepository;
import ru.belyaeva.rsoi.service.BillingService;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by user on 22.11.2016.
 */
@Service
public class BillingServiceImpl implements BillingService {

    @Autowired
    BillingRepository billingRepository;
    @Autowired
    UserRepository userRepository;

    // +/-
    @SneakyThrows
    @Override
    public BaseResponse createUserBilling(UserBillingInfo userBillingInfo) {
        UserEntity userEntity = userRepository.findByUserId(userBillingInfo.getUserId());
        BaseResponse baseResponse;

        if (userEntity != null) {// update userbilling
            userEntity.setMeansPayment(userBillingInfo.getMeansPayment());
            userEntity.setCardNumber(userBillingInfo.getCardNumber());
            userEntity.setCVV(userBillingInfo.getCVV());
            userEntity.setSummary(userBillingInfo.getSummary());
            userRepository.save(userEntity);
            baseResponse = new BaseResponse(true, "update data");
        } else {// create new billing user
            UserEntity new_userEntity = new UserEntity();
            new_userEntity.setMeansPayment(userBillingInfo.getMeansPayment());
            new_userEntity.setCardNumber(userBillingInfo.getCardNumber());
            new_userEntity.setCVV(userBillingInfo.getCVV());
            new_userEntity.setSummary(userBillingInfo.getSummary());
            new_userEntity.setUserId(userBillingInfo.getUserId());
            baseResponse = new BaseResponse(true, "create data");
            userRepository.save(new_userEntity);
        }


        return baseResponse;
    }

    // + | -
    @SneakyThrows
    @Override
    public BillingResponse executeBilling(Billing billingInfo) {

        BillingEntity billingEntity = new BillingEntity();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Date date = new Date();

        billingEntity.setBillingTime(dateFormat.format(date)); //2014/08/06 15:59:48
        billingEntity.setSummary(billingInfo.getSummary());
        billingEntity.setUserBilling(billingInfo.getUserBilling());

        billingRepository.save(billingEntity);

        BillingResponse billingResponse = new BillingResponse();
        Billing billing = new Billing();

        billing.setId(billingEntity.getId());
        billing.setBillingTime(billingEntity.getBillingTime()); //2014/08/06 15:59:48
        billing.setSummary(billingEntity.getSummary());
        billing.setUserBilling(billingEntity.getUserBilling());

        billingResponse.setBilling(billing);
        billingResponse.setCode(true);
        billingResponse.setMessage("");
        return billingResponse;
    }


}

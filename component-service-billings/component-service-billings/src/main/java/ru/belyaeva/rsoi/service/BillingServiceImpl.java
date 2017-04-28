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


import javax.persistence.EntityNotFoundException;
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

        if (userEntity != null) {// update userbilling
            userEntity.setMeansPayment(userBillingInfo.getMeansPayment());
            userEntity.setCardNumber(userBillingInfo.getCardNumber());
            userEntity.setCVV(userBillingInfo.getCVV());
            userEntity.setSummary(userBillingInfo.getSummary());
            userRepository.save(userEntity);

            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setErrorCode(true);
            baseResponse.setErrorMessage("update data");
            return baseResponse;

        } else {// create new billing user
            UserEntity new_userEntity = new UserEntity();
            new_userEntity.setMeansPayment(userBillingInfo.getMeansPayment());
            new_userEntity.setCardNumber(userBillingInfo.getCardNumber());
            new_userEntity.setCVV(userBillingInfo.getCVV());
            new_userEntity.setSummary(userBillingInfo.getSummary());
            new_userEntity.setUserId(userBillingInfo.getUserId());

            userRepository.save(new_userEntity);

            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setErrorCode(true);
            baseResponse.setErrorMessage("create data");
            return baseResponse;
        }

    }
    @SneakyThrows
    @Override
    public BillingResponse getBilling(Long userId, Long billingId)
    {
        BillingEntity billingEntity = billingRepository.findById(billingId);
        Billing billing = new Billing();

        billing.setId(billingEntity.getId());
        billing.setBillingTime(billingEntity.getBillingTime()); //2014/08/06 15:59:48
        billing.setSummary(billingEntity.getSummary());
        billing.setUserBilling(billingEntity.getUserBilling().getId());

        BillingResponse billingResponse = new BillingResponse();
        billingResponse.setBilling(billing);
        billingResponse.setCode(true);
        billingResponse.setMessage("");
        return billingResponse;
    }
    // + | -
    @SneakyThrows
    @Override
    public BillingResponse executeBilling(Double cost, Long UserId) {

        BillingEntity billingEntity = new BillingEntity();

        UserEntity userEntity = userRepository.findByUserId(UserId);
        if (userEntity == null)
        {
            BillingResponse billingResponse = new BillingResponse();
            billingResponse.setCode(false);
            billingResponse.setMessage("error user billing");
            return billingResponse;
        }

        BillingResponse billingResponse = new BillingResponse();
        // Проверка средств пользователя
        Double CommonSummary = userEntity.getSummary();
        if (CommonSummary < cost) // недостаток средств оплаты
        {
            billingResponse.setCode(false);
            billingResponse.setMessage("error summary");
            return billingResponse;
        }
        // изменение средств на счету пользователя
        userEntity.setSummary(CommonSummary - cost);
        userRepository.save(userEntity);
        // Добавление биллинга оплаты
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        billingEntity.setBillingTime(dateFormat.format(date)); //2014/08/06 15:59:48
        billingEntity.setSummary(cost);
        billingEntity.setUserBilling(userEntity);
        billingRepository.save(billingEntity);

        // ответ
        Billing billing = new Billing();

        billing.setId(billingEntity.getId());
        billing.setBillingTime(billingEntity.getBillingTime()); //2014/08/06 15:59:48
        billing.setSummary(billingEntity.getSummary());
        billing.setUserBilling(userEntity.getId());

        billingResponse.setBilling(billing);
        billingResponse.setCode(true);
        billingResponse.setMessage("");
        return billingResponse;
    }

    public BaseResponse returnBilling(Long userId, Long billingId)
    {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null)
        {
            BaseResponse baseResponse = new BaseResponse();

            baseResponse.setErrorCode(false);
            baseResponse.setErrorMessage("error user billing");
            return baseResponse;
        }
        BillingEntity billingEntity = billingRepository.findById(billingId);
        // возврат средств
        userEntity.setSummary(userEntity.getSummary() + billingEntity.getSummary());
        userRepository.save(userEntity);

        // удаление факта оплаты
        billingRepository.delete(billingId);
        BaseResponse baseResponse = new BaseResponse();

        baseResponse.setErrorCode(true);
        baseResponse.setErrorMessage("");
        return baseResponse;
    }

    public BaseResponse deleteBilling(Long billingId){
        BillingEntity trackEntity = billingRepository.findOne(billingId);
        if (trackEntity == null) {
            throw new EntityNotFoundException("billing bot exist");
        }
        billingRepository.delete(billingId);

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setErrorCode(true);
        baseResponse.setErrorMessage("");
        return baseResponse;

    }




}

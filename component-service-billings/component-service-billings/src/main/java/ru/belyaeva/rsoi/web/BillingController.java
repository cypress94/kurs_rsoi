package ru.belyaeva.rsoi.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.belyaeva.rsoi.model.*;
import ru.belyaeva.rsoi.service.BillingServiceImpl;

/**
 * Created by user on 22.11.2016.
 */
@Controller
@RequestMapping("/billingService")
public class BillingController {

    @Autowired
    BillingServiceImpl billingServiceImpl;

    @RequestMapping(value = "/user/{userId}/billing/create", method = RequestMethod.POST)
    @ResponseBody
    public BillingResponse executeBilling(
            @PathVariable ("userId") Long userId,
            @RequestBody Double cost) {
        return billingServiceImpl.executeBilling(cost, userId);
    }

    @RequestMapping(value = "/user/{userId}/billing/{billingId}", method = RequestMethod.GET)
    @ResponseBody
    public BillingResponse getBilling(
            @PathVariable ("userId") Long userId,
            @PathVariable ("billingId") Long billingId) {
        return billingServiceImpl.getBilling(userId, billingId);
    }

    @RequestMapping(value = "/user/{userId}/create", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse executeBilling(
            @PathVariable ("userId") Long userId,
            @RequestBody UserBillingInfo user) {
        return billingServiceImpl.createUserBilling(user);
    }



}

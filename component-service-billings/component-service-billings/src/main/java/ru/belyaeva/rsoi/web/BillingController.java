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
@RequestMapping("/billingService/")
public class BillingController {

    @Autowired
    BillingServiceImpl billingServiceImpl;

    @RequestMapping(value = "/billing/create", method = RequestMethod.POST)
    @ResponseBody
    public BillingResponse executeBilling(
            @RequestBody Billing billing) {
        return billingServiceImpl.executeBilling(billing);
    }

    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse executeBilling(
            @RequestBody UserBillingInfo user) {
        return billingServiceImpl.createUserBilling(user);
    }

}

package ru.belyaeva.rsoi.service;

import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.web.model.User;

/**
 * Created by user on 16.11.2016.
 */
@Service
public interface LoginService {

    Boolean register(User user);
    User checkAuthAndGetInfo(User user);
    Boolean checkLogin(User user);
}

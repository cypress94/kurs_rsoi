package ru.belyaeva.rsoi.service;

import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.web.model.UserInfo;
import ru.belyaeva.rsoi.web.model.UserInfoResponse;

/**
 * Created by user on 13.12.2016.
 */
@Service
public interface UserService {

    UserInfo create(UserInfo user);
    UserInfoResponse getUserInfo(String token);
}

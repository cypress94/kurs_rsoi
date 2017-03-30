package ru.belyaeva.rsoi.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.domain.UserEntity;
import ru.belyaeva.rsoi.repository.TokenRepository;
import ru.belyaeva.rsoi.repository.UserRepository;
import ru.belyaeva.rsoi.web.model.UserInfoResponse;
import ru.belyaeva.rsoi.domain.TokenEntity;
import ru.belyaeva.rsoi.web.model.UserInfo;

/**
 * Created by user on 13.12.2016.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenRepository tokenRepository;

    @Override
    public UserInfo create(UserInfo user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setClientId(user.getClientId());
        userEntity.setClientSecret(user.getClientSecret());
        userEntity.setUsername(user.getUserName());

     //   userEntity.setRedirectUrl(company.getRedirectUrl());

        userRepository.save(userEntity);

        return user;
    }
    @SneakyThrows
    @Override
    public UserInfoResponse getUserInfo(String token) {
        if (!checkToken(token)){
            throw new IllegalAccessException("Token error");
        }

        String tokenValue = token.replace("Bearer ", "");
        TokenEntity tokenEntity = tokenRepository.findByAccessToken(tokenValue);

        UserInfoResponse response = new UserInfoResponse();
        response.setEmail(tokenEntity.getUser().getEmail());
        response.setName(tokenEntity.getUser().getFirstname());
        return response;
    }

    @SneakyThrows
    private Boolean checkToken(String token) {
        if (!token.contains("Bearer")) {
            throw new IllegalAccessException("Unexpected format of access_token");
        }
        String tokenValue = token.replace("Bearer ", "");
        TokenEntity tokenEntity = tokenRepository.findByAccessToken(tokenValue);
        if (tokenEntity.getExpiresIn().compareTo(System.currentTimeMillis()) == -1) {
            throw new IllegalAccessException("Token has expired");
        }
        return true;
    }
}

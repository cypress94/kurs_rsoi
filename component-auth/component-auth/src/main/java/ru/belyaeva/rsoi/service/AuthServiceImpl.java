package ru.belyaeva.rsoi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.web.model.TokenResponse;
import ru.belyaeva.rsoi.domain.RequestEntity;
import ru.belyaeva.rsoi.domain.TokenEntity;
import ru.belyaeva.rsoi.domain.UserEntity;
import ru.belyaeva.rsoi.repository.RequestRepository;
import ru.belyaeva.rsoi.repository.TokenRepository;
import ru.belyaeva.rsoi.repository.UserRepository;
import ru.belyaeva.rsoi.web.model.CheckTokenRequest;
import ru.belyaeva.rsoi.web.model.CheckTokenResponse;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by user on 13.11.2016.
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public String getUserName(String clientId) {
        UserEntity userEntity = userRepository.findByClientId(clientId);
        if (userEntity == null) {
            throw new EntityNotFoundException("Client with id = " + clientId + " not found");
        }
        return userEntity.getUsername();
    }

    @Override
    public Long createRequest(String cliendId, String redirectUri) {
        UserEntity userEntity = userRepository.findByClientId(cliendId);
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setUser(userEntity);
        requestEntity.setRedirectUri(redirectUri);
        requestRepository.save(requestEntity);
        return requestEntity.getId();
    }

    @Override
    public Map<String, String> generateCode(String userName, Long requestId) {
        RequestEntity requestEntity = requestRepository.findById(requestId);
        String code = generateHeximalString(16);
        requestEntity.setCode(code);
        requestRepository.save(requestEntity);
        Map<String, String> result = new HashMap<String, String>();
        result.put("code", code);
        result.put("redirectUri", requestEntity.getRedirectUri());
        return result;
    }

    @Override
    public TokenResponse generateToken(String code) {

        RequestEntity requestEntity = requestRepository.findByCode(code);
        if (requestEntity == null) {
            throw new IllegalArgumentException("request param \"code\" is incorrect");
        }
        UserEntity userEntity = requestEntity.getUser();

        String accessToken = generateHeximalString(16);
        String refreshToken = generateHeximalString(16);
        Long expiresIn = System.currentTimeMillis() + 1800000L;
        String tokenType = "bearer";

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setAccessToken(accessToken);
        tokenEntity.setRefreshToken(refreshToken);
        tokenEntity.setExpiresIn(expiresIn);
        tokenEntity.setTokenType(tokenType);
        tokenEntity.setUser(userEntity);

        tokenRepository.save(tokenEntity);

        TokenResponse response = new TokenResponse();
        response.setAccessToken(tokenEntity.getAccessToken());
        response.setExpiresIn(String.valueOf(tokenEntity.getExpiresIn()));
        response.setRefreshToken(tokenEntity.getRefreshToken());
        response.setTokenType(tokenEntity.getTokenType());

        return response;

    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        TokenEntity tokenEntity = tokenRepository.findByRefreshToken(refreshToken);
        if (tokenEntity == null) {
            throw new IllegalArgumentException("request param \"refresh_token\" is incorrect");
        }

        String accessToken = generateHeximalString(16);
        String refreshTokenNew = generateHeximalString(16);
        Long expiresIn = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);

        tokenEntity.setAccessToken(accessToken);
        tokenEntity.setRefreshToken(refreshTokenNew);
        tokenEntity.setExpiresIn(expiresIn);
        tokenRepository.save(tokenEntity);

        TokenResponse response = new TokenResponse();
        response.setAccessToken(tokenEntity.getAccessToken());
        response.setExpiresIn(String.valueOf(tokenEntity.getExpiresIn()));
        response.setRefreshToken(tokenEntity.getRefreshToken());
        response.setTokenType(tokenEntity.getTokenType());

        return response;

    }


    @Override
    public String generateHeximalString(int hexLength) {

        Random randomService = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < hexLength) {
            sb.append(Integer.toHexString(randomService.nextInt()));
        }
        sb.setLength(hexLength);

        return sb.toString();
    }

    @Override
    public Boolean checkUserInfo(String clientId, String clientSecret) {
        UserEntity userEntity = userRepository.findByClientId(clientId);

        if (userEntity == null) {
            throw new EntityNotFoundException("User with client_id=" + clientId + " not found");
        }

        if (clientSecret != null && !clientSecret.isEmpty() && clientSecret.equals(userEntity.getClientSecret())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public CheckTokenResponse checkToken(CheckTokenRequest checkTokenRequest) {
        String token = checkTokenRequest.getToken();
        if (!token.contains("Bearer")) {
            CheckTokenResponse checkTokenResponse = new CheckTokenResponse();
            checkTokenResponse.setValidToken(false);
            return checkTokenResponse;
        }
        String tokenValue = token.replace("Bearer ", "");
        TokenEntity tokenEntity = tokenRepository.findByAccessToken(tokenValue);

        if (tokenEntity == null || tokenEntity.getExpiresIn().compareTo(System.currentTimeMillis()) == -1) {
            CheckTokenResponse checkTokenResponse = new CheckTokenResponse();
            checkTokenResponse.setValidToken(false);
            return checkTokenResponse;
        }

        CheckTokenResponse checkTokenResponse = new CheckTokenResponse();
        checkTokenResponse.setValidToken(true);
        checkTokenResponse.setUserId(tokenEntity.getUser().getId());
        return checkTokenResponse;


    }
}

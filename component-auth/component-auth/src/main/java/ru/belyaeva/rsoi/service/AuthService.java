package ru.belyaeva.rsoi.service;

import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.web.model.CheckTokenRequest;
import ru.belyaeva.rsoi.web.model.CheckTokenResponse;
import ru.belyaeva.rsoi.web.model.TokenResponse;

import java.util.Map;

/**
 * Created by user on 13.11.2016.
 */
@Service
public interface AuthService {

    String getUserName(String cliendId);

    Long createRequest(String clientId, String redirectUri);

    Map<String,String> generateCode(String userName, Long requestId);

    TokenResponse generateToken(String code);

    TokenResponse refreshToken(String refreshToken);

    String generateHeximalString(int size);

    Boolean checkUserInfo(String clientId, String clientSecret);

    CheckTokenResponse checkToken(CheckTokenRequest checkTokenRequest);

}

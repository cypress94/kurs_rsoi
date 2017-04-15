package ru.belyaeva.rsoi.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.domain.UserEntity;
import ru.belyaeva.rsoi.repository.UserRepository;
import ru.belyaeva.rsoi.web.model.User;

import java.util.Random;

/**
 * Created by user on 16.11.2016.
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    UserRepository userRepository;

    @Override
    public Boolean register(User user) {
        UserEntity userEntity = userRepository.findByUsername(user.getUsername());
        if (userEntity != null) {
            return false;
        }
        userEntity = new UserEntity();
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(user.getPassword());
        userEntity.setFirstname(user.getFirstNameReal());
        userEntity.setLastname(user.getLastNameReal());
        userEntity.setEmail(user.getEmail());
        userEntity.setMobile(user.getMobile());
        userEntity.setIsCourier(user.getIsCourier());
        userEntity.setDescription(user.getDescription());

        // добавление информации о юзере (внутренней)

        final Random random = new Random();
        String client_id = Integer.toString(random.nextInt(Integer.MAX_VALUE) + 1);
        String client_secret = "sec" + client_id;
        String url = "";

        userEntity.setClientId( client_id);
        userEntity.setClientSecret( client_secret );

        user.setClientId(client_id);
        user.setClientSecret(client_secret);
        userRepository.save(userEntity);

        return true;
    }

    @Override
    @SneakyThrows
    public User checkAuthAndGetInfo(User user) {
        UserEntity userEntity = userRepository.findByUsername(user.getUsername());

        if (userEntity != null && user.getPassword().equals(userEntity.getPassword())) {
            user.setEmail(userEntity.getEmail());
            user.setMobile(userEntity.getMobile());
            user.setIsCourier(userEntity.getIsCourier());
            user.setFirstNameReal(userEntity.getFirstname());
            user.setLastNameReal(userEntity.getLastname());
            user.setDescription(userEntity.getDescription());
            user.setClientId(userEntity.getClientId());
            user.setClientSecret(userEntity.getClientSecret());

            return user;
        } else {
            throw new Exception("Wrong username or password.");
        }

    }

    @Override
    public Boolean checkLogin(User user) {
        UserEntity userEntity = userRepository.findByUsername(user.getUsername());
        if (user.getPassword().equals(userEntity.getPassword())) {
            return true;
        } else {
            return false;
        }

    }
}

package me.cyril.mongodb.service;

import me.cyril.mongodb.repository.model.User;
import me.cyril.mongodb.repository.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by qianmin on 2017/2/17.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void save(User user){
        userRepository.insert(user);
    }
}

package me.cyril.mongodb.repository.repository;

import me.cyril.mongodb.repository.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * Created by qianmin on 2017/2/17.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String>{
}

package me.cyril.mongodb.repository.query;

import me.cyril.mongodb.repository.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by qianmin on 2017/2/17.
 */
@Component
public class UserQuery {

    private static final Logger logger = LoggerFactory.getLogger(UserQuery.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public void auditing(){
        User user = new User("qianmin");
        mongoTemplate.insert(user);
    }
}

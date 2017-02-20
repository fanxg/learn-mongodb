package me.cyril.mongodb.repository.repository;

import me.cyril.mongodb.ApplicationTest;
import me.cyril.mongodb.repository.model.User;
import me.cyril.mongodb.repository.query.UserQuery;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Created by qianmin on 2017/2/17.
 */
public class UserRepositoryTest extends ApplicationTest{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testInsert() throws InterruptedException {
        User user = userRepository.insert(new User("qianmin"));

        Thread.sleep(10);

        //TODO updateDate不生效
        userRepository.save(user);
        mongoTemplate.updateMulti(Query.query(Criteria.where("name").is("qianmin")),
                Update.update("name", "qianmin2"),
                User.class);
    }
}

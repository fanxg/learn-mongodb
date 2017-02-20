package me.cyril.mongodb.repository.query;

import me.cyril.mongodb.ApplicationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by qianmin on 2017/2/17.
 */
public class UserQueryTest extends ApplicationTest{

    @Autowired
    private UserQuery userQuery;

    @Test
    public void testAuditing(){
        userQuery.auditing();
    }
}

package me.cyril.mongodb.repository.query;

import me.cyril.mongodb.ApplicationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by qianmin on 2017/2/14.
 */
public class PersonQueryTest extends ApplicationTest {

    @Autowired
    private PersonQuery personQuery;

    @Test
    public void testSimpleMongoOperations(){
        personQuery.simpleMongoOperations();
    }

    @Test
    public void testMongoUpdateOperations(){
        personQuery.mongoUpdateOperations();
    }

    @Test
    public void testOptimisticLocking(){
        personQuery.optimisticLocking();
    }

    @Test
    public void testMongoQueryOperations(){
        personQuery.mongoQueryOperations();
    }
}

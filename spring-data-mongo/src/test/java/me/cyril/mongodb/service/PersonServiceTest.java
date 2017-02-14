package me.cyril.mongodb.service;

import me.cyril.mongodb.ApplicationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by qianmin on 2017/2/14.
 */
public class PersonServiceTest extends ApplicationTest{

    @Autowired
    private PersonService personService;

    @Test
    public void testQueryByExample(){
        personService.queryByExample();
    }
}

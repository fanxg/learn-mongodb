package me.cyril.mongodb.repository.repository;

import com.alibaba.fastjson.JSON;
import me.cyril.mongodb.ApplicationTest;
import me.cyril.mongodb.repository.model.Person;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

/**
 * Created by qianmin on 2017/2/17.
 */
public class PersonRepositoryTest extends ApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(PersonRepositoryTest.class);

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void init(){
        mongoTemplate.insert(new Person());
        mongoTemplate.insert(new Person("abc", 25));
        mongoTemplate.insert(new Person("abc", 26));
        mongoTemplate.insert(new Person("abc", 27));
        mongoTemplate.insert(new Person("abc", 33));
        mongoTemplate.insert(new Person("abc", 48));
        mongoTemplate.insert(new Person("abcd", 27));
        mongoTemplate.insert(new Person("abcd", 33));
        mongoTemplate.insert(new Person("abcd", 48));
        mongoTemplate.insert(new Person("abced", 27));
        mongoTemplate.insert(new Person("abced", 33));
        mongoTemplate.insert(new Person("abced", 48));
    }

    @After
    public void destroy(){
        mongoTemplate.dropCollection(Person.class);
    }

    @Test
    public void testCustomRepositoryMethod(){
        List<Person> persons1 = personRepository.findByName("abc");
        Assert.assertEquals(5, persons1.size());
        logger.info("person1:{}", JSON.toJSONString(persons1));

        Page<Person> persons2 = personRepository.findByName("abc",
                new PageRequest(1, 2, new Sort(Sort.Direction.DESC, "age")));
        Assert.assertEquals(5, persons2.getTotalElements());
        logger.info("person2:{}", JSON.toJSONString(persons2));

        List<Person> persons3 = personRepository.findByAgeGreaterThan(35);
        Assert.assertEquals(3, persons3.size());
        logger.info("person3:{}", JSON.toJSONString(persons3));

        List<Person> persons4 = personRepository.findByAgeBetween(32, 50);
        Assert.assertEquals(6, persons4.size());
        logger.info("person4:{}", JSON.toJSONString(persons4));

        List<Person> persons5 = personRepository.findByNameNull();
        Assert.assertEquals(1, persons5.size());
        logger.info("person5:{}", JSON.toJSONString(persons5));

        List<Person> persons6 = personRepository.findByNameNotNull();
        Assert.assertEquals(11, persons6.size());
        logger.info("person6:{}", JSON.toJSONString(persons6));

        List<Person> persons7 = personRepository.findByNameContaining("abc");
        Assert.assertEquals(11, persons7.size());
        logger.info("person7:{}", JSON.toJSONString(persons7));

        List<Person> persons8 = personRepository.deleteByName("abced");
        Assert.assertEquals(3, persons8.size());
        logger.info("person8:{}", JSON.toJSONString(persons8));
    }
}

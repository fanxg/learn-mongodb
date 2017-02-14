package me.cyril.mongodb.service;

import me.cyril.mongodb.repository.model.Person;
import me.cyril.mongodb.repository.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by qianmin on 2017/2/14.
 */
@Service
public class  PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void queryByExample(){
        mongoTemplate.insert(new Person("CyrilChien", 26));
        mongoTemplate.insert(new Person("CyrilChien", 27));

        Example<Person> example1 = Example.of(new Person("CyrilChien", 26));
        Person person = personRepository.findOne(example1);
        logger.info("person found. Person:{}", person);

        Example<Person> example2 = Example.of(new Person("CyrilChien"));
        List<Person> persons1 = personRepository.findAll(example2);
        logger.info("persons found. Person:{}", persons1);

        ExampleMatcher matcher3 = ExampleMatcher.matching()
                .withIgnoreCase()
                .withIgnorePaths("name")
                .withIncludeNullValues();
        Example<Person> example3 = Example.of(new Person("cyrilchien"), matcher3);
        List<Person> persons3 = personRepository.findAll(example3);
        logger.info("persons found. Person:{}", persons3);

        mongoTemplate.dropCollection(Person.class);
    }
}

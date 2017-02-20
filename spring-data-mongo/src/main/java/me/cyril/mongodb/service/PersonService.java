package me.cyril.mongodb.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.*;
import me.cyril.mongodb.repository.model.Person;
import me.cyril.mongodb.repository.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.data.mongodb.core.script.NamedMongoScript;
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
        mongoTemplate.insert(new Person());

        //1. find by example
        Example<Person> example1 = Example.of(new Person("CyrilChien", 26));
        Person person = personRepository.findOne(example1);
        logger.info("person found. {}", JSON.toJSONString(person));

        //2. find by example
        Example<Person> example2 = Example.of(new Person("CyrilChien"));
        List<Person> persons2 = personRepository.findAll(example2);
        logger.info("persons2 found. {}", JSON.toJSONString(persons2));

        //3. find by Example init with ExampleMatcher
        ExampleMatcher matcher3 = ExampleMatcher.matching()
                .withIgnoreCase() //.withIgnoreCase() 所有字段不区分大小写 .withIgnoreCase("name") 特定字段不区分大小写
                .withIgnorePaths("name") // 指定字段不作为查询条件
                .withIncludeNullValues(); //TODO
        Example<Person> example3 = Example.of(new Person("cyrilchien"), matcher3);
        List<Person> persons3 = personRepository.findAll(example3);
        logger.info("persons3 found. {}", JSON.toJSONString(persons3));

        //4. 通过lambda表示式构造ExampleMatcher 继而构造Example
        ExampleMatcher matcher4 = ExampleMatcher.matching()
                .withMatcher("name", match -> match.endsWith().ignoreCase());
        Example<Person> example4 = Example.of(new Person("chien", 26), matcher4);
        List<Person> persons4 = personRepository.findAll(example4);
        logger.info("persons4 found. {}", JSON.toJSONString(persons4));

        //5. 通过GenericPropertyMatchers静态方法构造ExampleMatcher 继而构造Example， 4,5效果相同
        ExampleMatcher matcher5 = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.endsWith().ignoreCase());
        Example<Person> example5 = Example.of(new Person("chien", 26), matcher5);
        List<Person> persons5 = personRepository.findAll(example5);
        logger.info("persons5 found. {}", JSON.toJSONString(persons5));

        //6. 通过StringMatcher构造ExampleMatcher 继而构造Example， 4,5,6效果相同
        ExampleMatcher matcher6 = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.ENDING)
                .withIgnoreCase();
        Example<Person> example6 = Example.of(new Person("chien", 26), matcher6);
        List<Person> persons6 = personRepository.findAll(example6);
        logger.info("persons6 found. {}", JSON.toJSONString(persons6));

        mongoTemplate.dropCollection(Person.class);
    }

    public void executeScript(){
        mongoTemplate.insert(new Person("CyrilChien", 26));
        mongoTemplate.insert(new Person("CyrilChien", 27));
        mongoTemplate.insert(new Person());

        ScriptOperations scriptOperations = mongoTemplate.scriptOps();
        ExecutableMongoScript script = new ExecutableMongoScript(
                "function(){return db.Person.find({}).toArray();}");
        //直接执行脚本
        Object result = scriptOperations.execute(script);
        logger.info("result: {}", JSON.toJSONString(result));

        //先注册后调用执行脚本
        scriptOperations.register(new NamedMongoScript("findAllPerson", script));
        result = scriptOperations.call("findAllPerson");
        logger.info("result: {}", JSON.toJSONString(result));


        mongoTemplate.dropCollection(Person.class);
    }

    public void executeCommand(){
        mongoTemplate.insert(new Person("CyrilChien", 26));
        mongoTemplate.insert(new Person("CyrilChien", 27));
        mongoTemplate.insert(new Person("qianmin", 27));

        CommandResult commandResult1 = mongoTemplate.executeCommand("{\"find\": \"Person\",\n" +
                "\t\"filter\": {\"name\":\"CyrilChien\"}\n" +
                "}");
        logger.info("commandResult1:{}", commandResult1);
        BasicDBObject basicDBObject1 = (BasicDBObject) commandResult1.get("cursor");
        logger.info("basicDBObject1:{}", basicDBObject1);

        DBObject command = new BasicDBObject();
        command.put("eval", "function(){return db.Person.find({\"name\":\"CyrilChien\"}).toArray();}");
        CommandResult commandResult2 = mongoTemplate.executeCommand(command);
        logger.info("commandResult2:{}", commandResult2);
        BasicDBList basicDBList2 = (BasicDBList) commandResult2.get("retval");
        logger.info("basicDBList2: {}", basicDBList2);

        mongoTemplate.dropCollection(Person.class);
    }
}

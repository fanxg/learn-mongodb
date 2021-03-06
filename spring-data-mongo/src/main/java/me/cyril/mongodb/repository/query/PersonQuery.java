package me.cyril.mongodb.repository.query;

import com.alibaba.fastjson.JSON;
import com.mongodb.WriteResult;
import me.cyril.mongodb.repository.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * Created by qianmin on 2017/2/13.
 */
@Component
public class PersonQuery {

    private static final Logger logger = LoggerFactory.getLogger(PersonQuery.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public void simpleMongoOperations() {
        //insert
        Person person = new Person("CyrilChien", 26);
        mongoTemplate.insert(person); //insert document with same id will throw error
        mongoTemplate.save(person); //insert document with same id will override
        logger.info("person inserted. Id: {}", person.getId());

        //find
        person = mongoTemplate.findById(person.getId(), Person.class);
        logger.info("person found. Person: {}", JSON.toJSONString(person));

        //update
        Query query = query(where("id").is(person.getId()));
        Update update = update("name", "QianMin").set("age", 27);
        mongoTemplate.updateFirst(query, update, Person.class);
        person = mongoTemplate.findById(person.getId(), Person.class);
        logger.info("person update. Person: {}", JSON.toJSONString(person));

        //delete
        mongoTemplate.remove(person);
        logger.info("person deleted. Person:{}", JSON.toJSONString(person));

        //drop collection
        mongoTemplate.dropCollection(Person.class);
        logger.info("collection person droped.");
    }

    public void mongoUpdateOperations(){
        //insert
        Person person = new Person("CyrilChien", 26);
        mongoTemplate.insert(person);
        logger.info("person inserted. Person: {}", JSON.toJSONString(person));  //"CyrilChien", 26

        //udpate
        mongoTemplate.updateFirst(query(where("id").is(person.getId())),
                update("name", "QianMin").inc("age", 1), Person.class);
        person = mongoTemplate.findById(person.getId(), Person.class);
        logger.info("person updated. Person: {}", JSON.toJSONString(person)); //"QianMin", 27

        //insert
        WriteResult result1 = mongoTemplate.upsert(query(where("name").is("CyrilChien").and("age").is(26)),
                update("name", "qm").set("age", 28), Person.class);
        logger.info("upsert result: {}", JSON.toJSONString(result1));
        person = mongoTemplate.findById(result1.getUpsertedId(), Person.class);
        logger.info("upsert person . Person: {}", JSON.toJSONString(person));  //"qm", 28

        //update
        WriteResult result2 = mongoTemplate.upsert(query(where("name").is("qm").and("age").is(28)),
                update("name", "qm").set("age", 29), Person.class);
        logger.info("upsert result: {}", JSON.toJSONString(result2));
        person = mongoTemplate.findById(result2.getUpsertedId(), Person.class);
        logger.info("upsert person . Person: {}", JSON.toJSONString(person));  //"qm", 29

        List<Person> persons = mongoTemplate.findAll(Person.class);
        logger.info("person list: {}", JSON.toJSONString(persons)); //"QianMin", 27; "qm", 29

        //update return old
        person = mongoTemplate.findAndModify(query(where("name").is("QianMin")),
                new Update().inc("age", 3), Person.class);
        logger.info("person update: {}", JSON.toJSONString(person)); //"QianMin", 27 (数据库中为"QianMin", 30)

        //update return new
        person = mongoTemplate.findAndModify(query(where("name").is("qm")),
                new Update().inc("age", 3),
                new FindAndModifyOptions().returnNew(true),
                Person.class);
        logger.info("person update: {}", JSON.toJSONString(person)); //"qm", 32

        mongoTemplate.dropCollection(Person.class);
    }

    public void optimisticLocking(){
        //insert
        Person person = new Person("CyrilChien", 26);
        mongoTemplate.insert(person);
        logger.info("person inserted. Person: {}", JSON.toJSONString(person));  //"age":26,"name":"CyrilChien","version":0, "id":"****"

        //update
        mongoTemplate.save(person);
        logger.info("person inserted. Person: {}", JSON.toJSONString(person));  //"age":26,"name":"CyrilChien","version":1, "id":"****"

        mongoTemplate.dropCollection(Person.class);
    }

    public void mongoQueryOperations(){
        mongoTemplate.insert(new Person("CyrilChien", 26));
        mongoTemplate.insert(new Person("QianMin", 27));

        //query with BasicQuery
        BasicQuery basicQuery = new BasicQuery("{\"age\" : {\"$lt\" : 30}, \"name\" : \"QianMin\"}");
        Person person1 = mongoTemplate.findOne(basicQuery, Person.class);
        logger.info("person found. Person: {}", JSON.toJSONString(person1));

        basicQuery = new BasicQuery("{\"age\" : {\"$lt\" : 30}}");
        List<Person> persons1 = mongoTemplate.find(basicQuery, Person.class);
        logger.info("persons found. Persons: {}", JSON.toJSONString(persons1));

        //query with Query, Criteria
        Query query = Query.query(Criteria.where("age").lt(30).and("name").is("QianMin"));
        Person person2 = mongoTemplate.findOne(query, Person.class);
        logger.info("person found. Person: {}", JSON.toJSONString(person2));

        query = Query.query(Criteria.where("age").lt(30));
        List<Person> persons2 = mongoTemplate.find(query, Person.class);
        logger.info("persons found. Persons: {}", JSON.toJSONString(persons2));

        logger.info(person1.equals(person2) + " " +  persons1.equals(persons2));

        //andOperator $and
        query = Query.query(new Criteria().andOperator(Criteria.where("age").lte(27), Criteria.where("age").gt(25)));
        List<Person> persons3 = mongoTemplate.find(query, Person.class);
        logger.info("persons found. Persons: {}", JSON.toJSONString(persons3));

        //filed, sort, skip, limit
        for(int index = 0; index < 10; index++){
            mongoTemplate.insert(new Person("name"+index, 30+index));
        }
        query = Query.query(Criteria.where("age").gte(30));
        query.fields().include("age").include("name").exclude("id");
        query.with(new Sort(Sort.Direction.DESC, "age"));
        query.skip(3).limit(3);
        List<Person> persons4 = mongoTemplate.find(query, Person.class);
        logger.info("persons found. Persons: {}", JSON.toJSONString(persons4));

        mongoTemplate.dropCollection(Person.class);
    }

    public void mongoAggregateOperations(){
        mongoTemplate.insert(new Person("CyrilChien", 26));
        mongoTemplate.insert(new Person("QianMin", 26));
        mongoTemplate.insert(new Person("QianMin", 31));
        mongoTemplate.insert(new Person("qm", 26));
        mongoTemplate.insert(new Person("CyrilChien", 28));

        Aggregation aggregation1 = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("age").is(26)),
                Aggregation.project("age", "name").andExclude("_id"),
                Aggregation.sort(new Sort(Sort.Direction.DESC, "name")),
                Aggregation.skip(1),
                Aggregation.limit(10)
        );
        List<Person> persons1 = mongoTemplate.aggregate(aggregation1, "Person", Person.class).getMappedResults();
        logger.info("found persons1. {}", JSON.toJSONString(persons1));

        Aggregation aggregation2 = Aggregation.newAggregation(
                Aggregation.group("name").count().as("count").sum("age").as("sumAge").avg("age").as("avgAge"),
                Aggregation.project("count", "sumAge", "avgAge").and("name").previousOperation()
        );
        List<PersonSummary> persons2 = mongoTemplate.aggregate(aggregation2, "Person", PersonSummary.class).getMappedResults();
        logger.info("found persons. {}", JSON.toJSONString(persons2));


        mongoTemplate.dropCollection(Person.class);
    }

    public void mongoIndexOperations(){
        mongoTemplate.dropCollection(Person.class);

        IndexOperations indexOperations = mongoTemplate.indexOps(Person.class);

        //添加索引
        indexOperations.ensureIndex(new Index().on("name", Sort.Direction.DESC).named("name_desc"));
        indexOperations.ensureIndex(new Index().on("name", Sort.Direction.DESC).on("age", Sort.Direction.DESC)); //复合索引

        //获取所有索引信息
        List<IndexInfo> indexInfos1 = indexOperations.getIndexInfo();
        logger.info("indexInfos: {}", JSON.toJSONString(indexInfos1));

        //删除索引
        indexOperations.dropIndex("name_desc");

        List<IndexInfo> indexInfos2 = indexOperations.getIndexInfo();
        logger.info("indexInfos: {}", JSON.toJSONString(indexInfos2));

        //删除所有索引
        indexOperations.dropAllIndexes();

        List<IndexInfo> indexInfos3 = indexOperations.getIndexInfo();
        logger.info("indexInfos: {}", JSON.toJSONString(indexInfos3)); //唯一索引_id不能被删除
    }

    class PersonSummary{
        private String name;
        private Double sumAge;
        private Double avgAge;
        private Integer count;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getSumAge() {
            return sumAge;
        }

        public void setSumAge(Double sumAge) {
            this.sumAge = sumAge;
        }

        public Double getAvgAge() {
            return avgAge;
        }

        public void setAvgAge(Double avgAge) {
            this.avgAge = avgAge;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}

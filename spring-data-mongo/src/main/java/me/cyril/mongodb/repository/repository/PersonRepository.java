package me.cyril.mongodb.repository.repository;

import me.cyril.mongodb.repository.model.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * Created by qianmin on 2017/2/14.
 */
@Repository
public interface PersonRepository extends MongoRepository<Person, String>, QueryByExampleExecutor<Person>{
}

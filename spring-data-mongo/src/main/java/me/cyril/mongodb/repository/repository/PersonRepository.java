package me.cyril.mongodb.repository.repository;

import me.cyril.mongodb.repository.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by qianmin on 2017/2/14.
 */
@Repository
public interface PersonRepository extends MongoRepository<Person, String>{

    List<Person> findByName(String name);

    Page<Person> findByName(String name, Pageable pageable);

    List<Person> findByAgeGreaterThan(int age);

    List<Person> findByAgeBetween(int from, int to);

    List<Person> findByNameNull();

    List<Person> findByNameNotNull();

    List<Person> findByNameContaining(String name);

    List<Person> deleteByName(String name);
}

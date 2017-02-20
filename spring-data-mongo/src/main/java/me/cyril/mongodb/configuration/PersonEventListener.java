package me.cyril.mongodb.configuration;

import com.alibaba.fastjson.JSON;
import me.cyril.mongodb.repository.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;

/**
 * 在数据转换、插入及查询前后做额外的操作
 *
 * Created by qianmin on 2017/2/17.
 */
@Component
public class PersonEventListener extends AbstractMongoEventListener<Person> {

    private static final Logger logger = LoggerFactory.getLogger(PersonEventListener.class);

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Person> event) {
        logger.info("before convert person");
        super.onBeforeConvert(event);
    }

    @Override
    public void onBeforeSave(BeforeSaveEvent<Person> event) {
        super.onBeforeSave(event);
        System.out.println("before save person");
    }
}

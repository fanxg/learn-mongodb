package me.cyril.mongodb.configuration;

import com.mongodb.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Created by qianmin on 2017/2/13.
 */
@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration implements ApplicationContextAware {

    private final static String MONGO_DB_NAME = "test";
    private final static String MONGO_URI = "mongodb://localhost:27017/test?readPreference=nearest&readPreferenceTags=dc:secondary&connectTimeoutMS=300000&w=1&maxPoolSize=100";
    private ApplicationContext applicationContext;

    @Override
    public Mongo mongo() throws Exception {
        MongoClientURI mongoClientURI = new MongoClientURI(MONGO_URI);
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        return mongoClient;
    }

    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongo(), MONGO_DB_NAME);
        return mongoDbFactory;
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), mappingMongoConverter());//定制映射转换器
        mongoTemplate.setWriteConcernResolver(customWriteConcernResolver()); //定制化WriteConcern
//        mongoTemplate.setWriteConcern(WriteConcern.W1); //全局WriteConcern,与上述定制化WriteConcern二选一
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG); //NONE, LOG, EXCEPTION
        mongoTemplate.setReadPreference(ReadPreference.nearest());//PRIMARY, SECONDARY, SECONDARY_PREFERRED, PRIMARY_PREFERRED, NEAREST
        mongoTemplate.setApplicationContext(applicationContext);//关联ApplicationContext
        return mongoTemplate;
    }

    public WriteConcernResolver customWriteConcernResolver() {
        WriteConcernResolver writeConcernResolver = new WriteConcernResolver() {
            @Override
            public WriteConcern resolve(MongoAction action) {
                if (action.getCollectionName().endsWith("Index") || action.getCollectionName().equals("Order")) {
                    return WriteConcern.ACKNOWLEDGED;
                } else {
                    return action.getDefaultWriteConcern();
                }
            }
        };
        return writeConcernResolver;
    }

    @Override
    public MappingMongoConverter mappingMongoConverter() throws Exception {
        MappingMongoConverter mappingMongoConverter = super.mappingMongoConverter();

        List<Converter> list = Arrays.asList(new BigDecimalToLongConverter(), new LongToBigDecimalConverter());
        mappingMongoConverter.setCustomConversions(new CustomConversions(list)); //type converter

        mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null)); //remove _class

        return mappingMongoConverter;
    }

    @Override
    protected String getDatabaseName() {
        return MONGO_DB_NAME;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

/**
 * 将BigDecimal数据转换成Long后持久化
 */
class BigDecimalToLongConverter implements Converter<BigDecimal, Long> {
    private BigDecimal CONST100 = new BigDecimal(100);

    @Override
    public Long convert(BigDecimal source) {
        return source.multiply(CONST100).longValue();
    }
}

/**
 * 读取时再反转为BigDecimal
 */
class LongToBigDecimalConverter implements Converter<Long, BigDecimal> {
    private BigDecimal CONST100 = new BigDecimal(100);

    @Override
    public BigDecimal convert(Long source) {
        return new BigDecimal(source).divide(CONST100).setScale(2);
    }
}
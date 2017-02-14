package me.cyril.mongodb;

/**
 * Created by qianmin on 2017/2/14.
 */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 两种方式启动SpringBootTest
 * 1.use @SpringBootTest(classes=...)
 * @RunWith(SpringRunner.class)
 * @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
 *
 * 2. use @ContextConfiguration
 * @RunWith(SpringRunner.class)
 * @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
 * @ContextConfiguration(classes = Application.class)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class ApplicationTest {
}

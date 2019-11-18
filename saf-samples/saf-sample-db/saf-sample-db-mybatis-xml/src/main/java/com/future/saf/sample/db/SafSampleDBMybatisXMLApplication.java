package com.future.saf.sample.db;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.future.saf.configcenter.apollo.EnableApolloConfigAutoChangePrint;
import com.future.saf.db.druid.EnableDBDruid;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by hepengyuan on 2018/09/27.
 */
@SpringBootApplication
@EnableApolloConfig(value = { "application", "saf.sample.mybatis" })
//开启两个datasource实例
@EnableDBDruid(beanName = "user", instance = "user", mapperPackages = "com.future.saf.db.mapper.userdb")
@EnableApolloConfigAutoChangePrint
@Slf4j
public class SafSampleDBMybatisXMLApplication {

	public static void main(String[] args) {
		SpringApplication.run(SafSampleDBMybatisXMLApplication.class, args);
	}

}

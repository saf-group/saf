package com.future.saf.db.druid.core;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.future.saf.core.CustomizedConfigurationPropertiesBinder;

public class DBDruidBeanPostProcessor implements BeanPostProcessor, Ordered, EnvironmentAware, BeanFactoryAware {

	private static String MYBATIS_CONFIG = "mybatis-config.xml";
	private static final Logger logger = LoggerFactory.getLogger(DBDruidBeanPostProcessor.class);
	public static final String PREFIX_APP_DATASOURCE = "db";

	@Autowired
	private CustomizedConfigurationPropertiesBinder binder;

	private Environment environment;

	private BeanFactory beanFactory;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

		String instance = DBDruidRegistrar.instanceMap.get(beanName);

		if (bean instanceof DruidDataSource) {

			DruidDataSource druidDataSource = (DruidDataSource) bean;
			initDataSource(druidDataSource);
			if (environment.containsProperty(PREFIX_APP_DATASOURCE + "." + instance + ".data-source" + ".filters")) {
				druidDataSource.clearFilters();
			}
			Bindable<?> target = Bindable.of(DruidDataSource.class).withExistingValue(druidDataSource);
			binder.bind(PREFIX_APP_DATASOURCE + "." + instance + ".data-source", target);

		} else if (bean instanceof SqlSessionFactoryBean) {

			SqlSessionFactoryBean sqlSessionFactoryBean = (SqlSessionFactoryBean) bean;

			DataSource dataSource = beanFactory.getBean(instance + DataSource.class.getSimpleName(), DataSource.class);
			String typeAliasesPackageKey = PREFIX_APP_DATASOURCE + "." + instance + ".type-aliases-package";
			String typeAliasesPackage = environment.getProperty(typeAliasesPackageKey);
			Assert.isTrue(StringUtils.isNotEmpty(typeAliasesPackage),
					String.format("%s=%s must be not null! ", typeAliasesPackageKey, typeAliasesPackage));
			initSqlSessionFactoryBean(dataSource, typeAliasesPackage, sqlSessionFactoryBean);

		} else if (bean instanceof DataSourceTransactionManager) {

			DataSourceTransactionManager dataSourceTransactionManager = (DataSourceTransactionManager) bean;

			DataSource dataSource = beanFactory.getBean(instance + DataSource.class.getSimpleName(), DataSource.class);

			dataSourceTransactionManager.setDataSource(dataSource);
			dataSourceTransactionManager.afterPropertiesSet();

		}
		return bean;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	private void initSqlSessionFactoryBean(DataSource dataSource, String typeAliasesPackage,
			SqlSessionFactoryBean sqlSessionFactoryBean) {
		sqlSessionFactoryBean.setDataSource(dataSource);
		sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(MYBATIS_CONFIG));
		if (StringUtils.isNotEmpty(typeAliasesPackage)) {
			sqlSessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);
		}
	}

	private void initDataSource(DruidDataSource datasource) {
		/**
		 * https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8
		 */
		datasource.setDriverClassName("com.mysql.jdbc.Driver");
		datasource.setInitialSize(1);
		datasource.setMinIdle(1);
		datasource.setMaxActive(5);
		datasource.setMaxWait(60000);
		datasource.setTimeBetweenEvictionRunsMillis(60000);
		datasource.setMinEvictableIdleTimeMillis(300000);
		datasource.setTimeBetweenLogStatsMillis(30000);
		datasource.setValidationQuery("SELECT 'x'");
		datasource.setTestWhileIdle(true);
		datasource.setTestOnBorrow(false);
		datasource.setTestOnReturn(false);
		try {
			datasource.setFilters("config,wall");
		} catch (SQLException e) {
			logger.error("druid configuration initialization filter", e);
		}
	}

}

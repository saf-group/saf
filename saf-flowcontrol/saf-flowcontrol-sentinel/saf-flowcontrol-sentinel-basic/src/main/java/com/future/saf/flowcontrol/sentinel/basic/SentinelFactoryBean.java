package com.future.saf.flowcontrol.sentinel.basic;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import com.future.saf.core.CustomizedConfigurationPropertiesBinder;
import com.future.saf.flowcontrol.sentinel.basic.exception.SentinelBeanInitException;

public class SentinelFactoryBean implements FactoryBean<AbstractSentinelHolder>, EnvironmentAware, BeanNameAware {

	private static final String SENTINEL_APOLLO_KEY_PREFIX = "sentinel";

	private String beanName;

	@SuppressWarnings("unused")
	private Environment environment;

	@Autowired
	protected CustomizedConfigurationPropertiesBinder binder;

	@Override
	public AbstractSentinelHolder getObject() throws Exception {

		String datasource = SentinelRegistrar.datasourceMap.get(beanName);

		AbstractSentinelHolder holder = null;

		// 目前datasource只支持apollo
		if ("apollo".equals(datasource)) {
			holder = (AbstractSentinelHolder) Class
					.forName("com.future.saf.flowcontrol.sentinel.ext.apollo.core.SentinelExtApolloHolder")
					.newInstance();

			Bindable<?> target = Bindable.of(AbstractSentinelHolder.class).withExistingValue(holder);
			binder.bind(SENTINEL_APOLLO_KEY_PREFIX, target);
			holder.load();
		} else {
			throw new SentinelBeanInitException(
					"AbstractSentinelHolder init failed. No found datasource:" + datasource);
		}

		return holder;
	}

	@Override
	public Class<?> getObjectType() {
		return SentinelFactoryBean.class;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

}

package com.future.saf.flowcontrol.sentinel.basic;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;

import com.future.saf.core.CustomizedConfigurationPropertiesBinder;
import com.future.saf.flowcontrol.sentinel.basic.exception.SentinelBeanInitException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SentinelFactoryBean implements FactoryBean<AbstractSentinelHolder>, BeanNameAware {

	private static final String SENTINEL_APOLLO_KEY_PREFIX = "sentinel";

	private String beanName;

	@Autowired
	protected CustomizedConfigurationPropertiesBinder binder;

	@Override
	public AbstractSentinelHolder getObject() throws Exception {

		String datasource = SentinelRegistrar.datasourceMap.get(beanName);
		String project = SentinelRegistrar.projectMap.get(beanName);
		String instance = SentinelRegistrar.instanceMap.get(beanName);

		AbstractSentinelHolder holder = null;

		// 目前datasource只支持apollo
		if ("apollo".equals(datasource)) {
			holder = (AbstractSentinelHolder) Class
					.forName("com.future.saf.flowcontrol.sentinel.ext.apollo.core.SentinelExtApolloHolder")
					.newInstance();

			Bindable<?> target = Bindable.of(AbstractSentinelHolder.class).withExistingValue(holder);
			binder.bind(SENTINEL_APOLLO_KEY_PREFIX + "." + project + "." + instance, target);
			holder.init();
		} else {
			log.error("AbstractSentinelHolder init failed. No found datasource:" + datasource);
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

}

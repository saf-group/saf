package com.future.saf.http.apache.httpcomponents.syncimpl;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import com.future.saf.core.CustomizedConfigurationPropertiesBinder;

public class ApacheHttpcomponentsBioClientFactoryBean
		implements FactoryBean<ApacheHttpcomponentsBioClient>, EnvironmentAware, BeanNameAware {

	public static final String PREFIX = "apache-httpcomponents.bio-client";

	@SuppressWarnings("unused")
	private Environment environment;
	private String beanName;

	@Autowired
	protected CustomizedConfigurationPropertiesBinder binder;

	@Override
	public ApacheHttpcomponentsBioClient getObject() {

		String instance = ApacheHttpcomponentsBioClientRegistrar.instanceMap.get(beanName);
		String project = ApacheHttpcomponentsBioClientRegistrar.projectMap.get(beanName);

		ApacheHttpcomponentsBioClientProps props = new ApacheHttpcomponentsBioClientProps();
		Bindable<?> target = Bindable.of(ApacheHttpcomponentsBioClientProps.class).withExistingValue(props);
		binder.bind(project + "." + getPreFix() + "." + instance + ".props", target);

		ApacheHttpcomponentsBioClient cHttpBioClient = new ApacheHttpcomponentsBioClient(instance.replace("-", "_"),
				props);

		return cHttpBioClient;
	}

	private String getPreFix() {
		return PREFIX;
	}

	@Override
	public Class<?> getObjectType() {
		return ApacheHttpcomponentsBioClient.class;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}
}

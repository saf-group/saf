package com.future.saf.http.apache.httpcomponents.syncimpl;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import com.future.saf.core.CustomizedConfigurationPropertiesBinder;

public class HttpBioClientFactoryBean implements FactoryBean<HttpBioClient>, EnvironmentAware, BeanNameAware {

	public static final String PREFIX = "http-bio-client";

	@SuppressWarnings("unused")
	private Environment environment;
	private String beanName;

	@Autowired
	protected CustomizedConfigurationPropertiesBinder binder;

	@Override
	public HttpBioClient getObject() {

		String instance = HttpBioClientRegistrar.instanceMap.get(beanName);
		String project = HttpBioClientRegistrar.projectMap.get(beanName);

		HttpBioClientProps props = new HttpBioClientProps();
		Bindable<?> target = Bindable.of(HttpBioClientProps.class).withExistingValue(props);
		binder.bind(project + "." + getPreFix() + "." + instance + ".props", target);

		HttpBioClient cHttpBioClient = new HttpBioClient(instance.replace("-", "_"), props);

		return cHttpBioClient;
	}

	private String getPreFix() {
		return PREFIX;
	}

	@Override
	public Class<?> getObjectType() {
		return HttpBioClient.class;
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

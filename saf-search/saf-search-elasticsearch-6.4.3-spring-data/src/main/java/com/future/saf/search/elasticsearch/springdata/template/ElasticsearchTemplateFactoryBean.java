package com.future.saf.search.elasticsearch.springdata.template;

import java.net.InetAddress;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import com.alibaba.fastjson.JSONObject;
import com.future.saf.configcenter.basic.ConfigCenterBasicService;
import com.future.saf.core.CustomizedConfigurationPropertiesBinder;
import com.future.saf.search.elasticsearch.ElasticsearchClientConfig;
import com.future.saf.search.elasticsearch.ElasticsearchConstant;
import com.future.saf.search.exception.SearchBeanInitException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElasticsearchTemplateFactoryBean
		implements FactoryBean<ElasticsearchTemplate>, BeanNameAware, ApplicationContextAware {
	private String beanName;

	private ApplicationContext applicationContext;

	@Autowired
	protected CustomizedConfigurationPropertiesBinder binder;

	@Override
	public ElasticsearchTemplate getObject() throws Exception {

		String clusterName = ElasticsearchTemplateRegistrar.templateBeanNameToClusterNameMap.get(beanName);

		ElasticsearchClientConfig esClientConfig = new ElasticsearchClientConfig();
		Bindable<?> target = Bindable.of(ElasticsearchClientConfig.class).withExistingValue(esClientConfig);

		binder.bind(ElasticsearchConstant.ELASTICSEARCH_INGEST_CONFIG_PREFIX + "." + clusterName, target);

		// 获取apollo中的配置
		String esServersStr = esClientConfig.getElasticsearchServers();
		if ("null".equalsIgnoreCase(esServersStr) || StringUtils.isEmpty(esServersStr)) {
			throw new SearchBeanInitException("elasticsearch server address were not configed.");
		}

		String[] esServers = esServersStr.split(",");

		TransportClient client = new PreBuiltTransportClient(loadElasticsearchClientConfig(clusterName));

		for (String server : esServers) {
			String[] hostAndPort = server.split(":");
			client.addTransportAddress(
					new TransportAddress(InetAddress.getByName(hostAndPort[0]), Integer.parseInt(hostAndPort[1])));
		}

		ElasticsearchTemplate template = new ElasticsearchTemplate(client);
		return template;
	}

	@Override
	public Class<?> getObjectType() {
		return ElasticsearchTemplateFactoryBean.class;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	public Settings loadElasticsearchClientConfig(String clusterName) {

		ConfigCenterBasicService configService = applicationContext.getBean(ConfigCenterBasicService.class);

		JSONObject apolloConfig = configService.getProperties(
				ElasticsearchConstant.ELASTICSEARCH_INGEST_CONFIG_PREFIX + "." + clusterName + ".clientConfig");
		Set<String> propertyNames = apolloConfig.keySet();

		Builder builder = Settings.builder();
		for (String key : propertyNames) {
			String originValue = apolloConfig.getString(key);

			if (originValue == null) {
				log.error(new StringBuilder("apollo config key:").append(key)
						.append(" is configed invalied, originValue is ").append(originValue).toString());
				continue;
			}

			String[] array = originValue.split(";");
			int len = array.length;

			// array的合法长度只有2,3.
			// 1;time;timeunit
			// 1024;bytesize;bytesizeunit
			// 1;byte/short/int/long/float/double/boolean/char
			if (len == 2) {
				loadValue(builder, key, array[0], array[1], null, originValue);
			} else if (len == 3) {
				loadValue(builder, key, array[0], array[1], array[2], originValue);
			} else {
				log.error(new StringBuilder("apollo config key:").append(key)
						.append(" is configed invalied, size invalid. originValue is ").append(originValue).toString());
				continue;
			}
		}
		return builder.build();

	}

	public void loadValue(Builder builder, String key, String value, String valueType, String valueUnit,
			String originValue) {
		try {
			if ("time".equalsIgnoreCase(valueType)) {
				if (TimeUnit.DAYS.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), TimeUnit.DAYS);
				} else if (TimeUnit.HOURS.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), TimeUnit.HOURS);
				} else if (TimeUnit.MICROSECONDS.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), TimeUnit.MICROSECONDS);
				} else if (TimeUnit.MILLISECONDS.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), TimeUnit.MILLISECONDS);
				} else if (TimeUnit.MINUTES.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), TimeUnit.MINUTES);
				} else if (TimeUnit.NANOSECONDS.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), TimeUnit.NANOSECONDS);
				} else if (TimeUnit.SECONDS.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), TimeUnit.SECONDS);
				} else {
					log.error(new StringBuilder("apollo config key:").append(key)
							.append(" is configed invalied, originValue is ").append(originValue).toString());
				}
			} else if ("bytesize".equalsIgnoreCase(valueType)) {
				if (ByteSizeUnit.BYTES.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), ByteSizeUnit.BYTES);
				} else if (ByteSizeUnit.BYTES.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), ByteSizeUnit.BYTES);
				} else if (ByteSizeUnit.GB.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), ByteSizeUnit.GB);
				} else if (ByteSizeUnit.KB.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), ByteSizeUnit.KB);
				} else if (ByteSizeUnit.MB.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), ByteSizeUnit.MB);
				} else if (ByteSizeUnit.PB.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), ByteSizeUnit.PB);
				} else if (ByteSizeUnit.TB.toString().equalsIgnoreCase(valueUnit)) {
					builder.put(key, Long.parseLong(value), ByteSizeUnit.TB);
				} else {
					log.error(new StringBuilder("apollo config key:").append(key)
							.append(" is configed invalied, originValue is ").append(originValue).toString());
				}
			}
			// byte/short/int/long/float/double/boolean/char
			else if ("int".equalsIgnoreCase(valueType) || "integer".equalsIgnoreCase(valueType)) {
				builder.put(key, Integer.parseInt(value));
			} else if ("long".equalsIgnoreCase(valueType)) {
				builder.put(key, Long.parseLong(value));
			} else if ("float".equalsIgnoreCase(valueType)) {
				builder.put(key, Float.parseFloat(value));
			} else if ("double".equalsIgnoreCase(valueType)) {
				builder.put(key, Double.parseDouble(value));
			} else if ("boolean".equalsIgnoreCase(valueType) || "bool".equalsIgnoreCase(valueType)) {
				builder.put(key, Boolean.parseBoolean(value));
			} else if ("string".equalsIgnoreCase(valueType)) {
				builder.put(key, value);
			} else {
				log.error(new StringBuilder("apollo config key:").append(key)
						.append(" is configed invalied, originValue is ").append(originValue).toString());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}

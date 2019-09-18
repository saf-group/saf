package com.future.saf.configcenter.apollo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass({ EnableApolloConfig.class })
public final class ApolloConfigAutoChangePrintAutoConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(ApolloConfigAutoChangePrintAutoConfiguration.class);

	@PostConstruct
	private void initialize() {
		Set<String> namespaces = ApolloAutoChangePrintRegistrar.namespaceSet;
		if (!CollectionUtils.isEmpty(namespaces)) {
			Config config = null;
			Map<String, String> map = null;
			for (String namespace : namespaces) {

				config = ConfigService.getConfig(namespace);
				if (config != null) {

					Set<String> propNames = config.getPropertyNames();
					if (!CollectionUtils.isEmpty(propNames)) {
						map = new HashMap<String, String>();
						for (String propName : propNames) {
							map.put(String.format("%s", propName),
									String.format("%s", config.getProperty(propName, null)));
						}
						log.info(String.format("initialize config, namespace:%s; values:%s", namespace, JSON
								.toJSONString(map, SerializerFeature.PrettyFormat, SerializerFeature.WriteClassName)));
					}
				}
			}
		}
	}

	@ApolloConfigChangeListener
	private void onChange(ConfigChangeEvent changeEvent) {
		try {
			Set<String> changedKeys = changeEvent.changedKeys();
			Map<String, String> map = new HashMap<String, String>();
			ConfigChange cc = null;
			for (String changedKey : changedKeys) {
				cc = changeEvent.getChange(changedKey);
				if (cc != null) {
					map.put(String.format("ns:%s; propName:%s", cc.getNamespace(), changedKey),
							String.format("oldValue:%s->newValue:%s", cc.getOldValue(), cc.getNewValue()));
				}
			}
			log.info("changed config:"
					+ JSON.toJSONString(map, SerializerFeature.PrettyFormat, SerializerFeature.WriteClassName));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}

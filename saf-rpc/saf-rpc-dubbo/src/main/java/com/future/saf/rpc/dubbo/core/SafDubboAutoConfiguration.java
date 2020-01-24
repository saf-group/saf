package com.future.saf.rpc.dubbo.core;

//import org.apache.dubbo.config.ApplicationConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.slf4j.Logger;
//import com.future.saf.logging.basic.Loggers;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.future.saf.core.autoconfiguration.CustomizedPropertiesBinderAutoConfiguration;

@Configuration
@AutoConfigureAfter(CustomizedPropertiesBinderAutoConfiguration.class)
public class SafDubboAutoConfiguration {

	// private static final Logger logger = Loggers.getFrameworkLogger();

}

package com.future.saf.logging.apollo;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.future.saf.logging.basic.LogConstant;

@ConditionalOnClass({ EnableApolloConfig.class })
public class LoggerLevelRefresherAutoConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(LoggerLevelRefresherAutoConfiguration.class);

	@ApolloConfig("saf.log.level")
	private Config config;

	@PostConstruct
	private void initialize() {
		try {
			refreshLoggingLevels(config.getPropertyNames());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@ApolloConfigChangeListener(value = { "saf.log.level" })
	private void onChange(ConfigChangeEvent changeEvent) {
		try {
			refreshLoggingLevels(changeEvent);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private final String LOGGER_PREFIX = "app.logging.level.";

	private void refreshLoggingLevels(Set<String> changedKeys) {
		for (String changedKey : changedKeys) {
			if (!changedKey.startsWith(LOGGER_PREFIX)) {
				continue;
			}

			String level = config.getProperty(changedKey, null);
			if (level == null) {
				continue;
			}

			if (!LogConstant.LOGGER_LEVEL_LIST.contains(level)) {
				logger.error("wrong level from apollo. logger:" + changedKey + "level:" + level);
				continue;
			}

			level = level.toUpperCase();
			changedKey = changedKey.replace(LOGGER_PREFIX, "");
			boolean changeSuccess = setLevel(changedKey, level);

			if (changeSuccess) {
				logger.info("level from apollo.logger:" + changedKey + ";level:" + level);
			}
		}
	}

	private void refreshLoggingLevels(ConfigChangeEvent changeEvent) {

		Set<String> changedKeys = changeEvent.changedKeys();

		for (String changedKey : changedKeys) {

			if (!changedKey.startsWith(LOGGER_PREFIX)) {
				continue;
			}

			ConfigChange cc = changeEvent.getChange(changedKey);
			String newLevel = cc.getNewValue();
			if (StringUtils.isEmpty(newLevel)) {
				continue;
			}

			if (!LogConstant.LOGGER_LEVEL_LIST.contains(newLevel)) {
				logger.error("wrong level from apollo. logger:" + changedKey + "; oldLevel:" + cc.getOldValue()
						+ ";newLevel:" + cc.getNewValue());
				continue;
			}

			newLevel = newLevel.toUpperCase();
			changedKey = changedKey.replace(LOGGER_PREFIX, "");

			boolean changeSuccess = setLevel(changedKey, newLevel);

			if (changeSuccess) {
				logger.info("level changed from apollo. logger:" + changedKey + "; oldLevel:" + cc.getOldValue()
						+ ";newLevel:" + cc.getNewValue());
			}

		}
	}

	private boolean setLevel(String loggerName, String newLevel) {

		boolean changeSuccess = false;

		if (LogConstant.LOGGER_COMMON_LIST.contains(loggerName)) {
			Configurator.setAllLevels(loggerName, Level.getLevel(newLevel));
			changeSuccess = true;
		} else if (LogConstant.LOGGER_ROOT.equals(loggerName)) {
//			Configurator.setAllLevels(loggerName, Level.getLevel(newLevel));8080
//			changeSuccess = true;
		}

		return changeSuccess;
	}

}

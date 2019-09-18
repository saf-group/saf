package com.future.saf.core.preloader;

import com.google.common.collect.Sets;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.Set;

public class PreloaderRegistry implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	public Set<Preloader> preloaders() {
		Set<Preloader> preloaders = Sets.newConcurrentHashSet();
		final Map<String, Preloader> beansOfType = applicationContext.getBeansOfType(Preloader.class);
		preloaders.clear();
		preloaders.addAll(beansOfType.values());
		return preloaders;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}

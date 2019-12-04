package com.future.saf.configcenter.apollo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;

public final class ApolloConfigAutoChangePrintProcessor implements BeanPostProcessor, PriorityOrdered {

	private static final Logger logger = LoggerFactory.getLogger(ApolloConfigAutoChangePrintProcessor.class);

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Class<?> clazz = bean.getClass();
		for (Method method : findAllMethod(clazz)) {
			processMethod(bean, beanName, method);
		}
		return bean;
	}

	@Override
	public int getOrder() {
		return PriorityOrdered.LOWEST_PRECEDENCE - 1;
	}

	private List<Method> findAllMethod(Class<?> clazz) {
		final List<Method> res = new LinkedList<>();
		ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				res.add(method);
			}
		});
		return res;
	}

	@SuppressWarnings("unchecked")
	private void processMethod(final Object bean, String beanName, final Method method) {
		ApolloConfigChangeListener annotation = AnnotationUtils.findAnnotation(method,
				ApolloConfigChangeListener.class);

		if (annotation == null) {
			return;
		}

//		if (beanName.equals(LoggerLevelRefresherAutoConfiguration.class.getName())) {
//			return;
//		}

		if (!CollectionUtils.isEmpty(ApolloAutoChangePrintRegistrar.namespaceSet)) {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
			try {
				Field declaredField = invocationHandler.getClass().getDeclaredField("memberValues");
				// 因为这个字段事 private final 修饰，所以要打开权限
				declaredField.setAccessible(true);
				@SuppressWarnings("rawtypes")
				Map namespaces = (Map) declaredField.get(invocationHandler);

				int size = ApolloAutoChangePrintRegistrar.namespaceSet.size();
				String[] valuearray = new String[size];
				int i = 0;
				for (String ns : ApolloAutoChangePrintRegistrar.namespaceSet) {
					valuearray[i++] = ns;
				}

				namespaces.put("value", valuearray);
				logger.info("autoChnage.namespaces:" + annotation.value());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

}

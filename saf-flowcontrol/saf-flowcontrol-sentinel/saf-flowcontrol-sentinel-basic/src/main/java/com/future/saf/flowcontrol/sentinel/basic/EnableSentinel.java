package com.future.saf.flowcontrol.sentinel.basic;

import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(SentinelRegistrar.class)
public @interface EnableSentinel {

	public String beanNamePrefix() default "default";

	public String datasource() default "apollo";
}
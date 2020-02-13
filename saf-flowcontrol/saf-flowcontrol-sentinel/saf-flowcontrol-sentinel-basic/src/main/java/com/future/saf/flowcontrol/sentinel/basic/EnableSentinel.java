package com.future.saf.flowcontrol.sentinel.basic;

import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableSentinels.class)
@Import(SentinelRegistrar.class)
public @interface EnableSentinel {

	public String beanNamePrefix() default "default";

	public String instance() default "default";

	public String project();

	public String datasource() default "apollo";
}
package com.future.saf.http.apache.httpcomponents.syncimpl;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableApacheHttpcomponentsBioClients.class)
@Import(ApacheHttpcomponentsBioClientRegistrar.class)
public @interface EnableApacheHttpcomponentsBioClient {

	String beanNamePrefix() default "default";

	String instance();

	String project();
}
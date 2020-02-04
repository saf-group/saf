package com.future.saf.http.apache.httpcomponents.syncimpl;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableHttpBioClients.class)
@Import(HttpBioClientRegistrar.class)
public @interface EnableHttpBioClient {

	String beanNamePrefix() default "default";

	String instance();
}
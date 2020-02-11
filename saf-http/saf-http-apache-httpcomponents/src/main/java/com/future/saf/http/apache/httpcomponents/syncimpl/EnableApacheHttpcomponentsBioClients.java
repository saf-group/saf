package com.future.saf.http.apache.httpcomponents.syncimpl;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ApacheHttpcomponentsBioClientRegistrar.class)
public @interface EnableApacheHttpcomponentsBioClients {

	EnableApacheHttpcomponentsBioClient[] value();

}
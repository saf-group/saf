package com.future.saf.search.elasticsearch.springdata.template;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ElasticsearchTemplateRegistrar.class)
public @interface EnableElasticsearchTemplates {

	EnableElasticsearchTemplate[] value();

}
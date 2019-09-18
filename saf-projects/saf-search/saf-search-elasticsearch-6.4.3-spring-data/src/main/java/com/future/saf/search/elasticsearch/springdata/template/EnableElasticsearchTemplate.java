package com.future.saf.search.elasticsearch.springdata.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableElasticsearchTemplates.class)
@Import(ElasticsearchTemplateRegistrar.class)
public @interface EnableElasticsearchTemplate {

	public String clusterName() default "clusterName";

}
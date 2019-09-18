package com.future.saf.rpc.motan;

import org.springframework.context.annotation.Import;

import com.future.saf.rpc.motan.core.MotanRegistrar;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableMotans.class)
@Import(MotanRegistrar.class)
public @interface EnableMotan {

	public String beanNamePrefix() default "defaultMotan";

	public String instance() default "defaultInstance";
}
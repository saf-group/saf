package com.future.saf.rpc.motan;

import org.springframework.context.annotation.Import;

import com.future.saf.rpc.motan.core.MotanRegistrar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MotanRegistrar.class)
public @interface EnableMotans {
	EnableMotan[] value();
}
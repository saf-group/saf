package com.future.saf.db.druid;

import org.springframework.context.annotation.Import;

import com.future.saf.db.druid.core.DBDruidRegistrar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(DBDruidRegistrar.class)
public @interface EnableDBDruids {

	EnableDBDruid[] value();

}
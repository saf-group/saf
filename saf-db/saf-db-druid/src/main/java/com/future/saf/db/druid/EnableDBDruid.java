package com.future.saf.db.druid;

import org.springframework.context.annotation.Import;

import com.future.saf.db.druid.core.DBDruidRegistrar;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableDBDruids.class)
@Import(DBDruidRegistrar.class)
public @interface EnableDBDruid {

	public String beanName() default "defaultDBDruid";

	public String instance() default "defaultInstance";

	public String[] mapperPackages() default {};

}
package com.future.saf.rpc.dubbo;

import org.springframework.context.annotation.Import;
import com.future.saf.rpc.dubbo.core.SafDubboRegistrar;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableSafDubbos.class)
@Import(SafDubboRegistrar.class)
public @interface EnableSafDubbo {

	public String beanNamePrefix() default "defaultMotan";

	public String instance() default "defaultInstance";

	public String project() default "defaultProject";
}
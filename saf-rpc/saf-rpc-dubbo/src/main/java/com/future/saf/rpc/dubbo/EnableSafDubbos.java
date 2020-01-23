package com.future.saf.rpc.dubbo;

import org.springframework.context.annotation.Import;
import com.future.saf.rpc.dubbo.core.SafDubboRegistrar;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(SafDubboRegistrar.class)
public @interface EnableSafDubbos {
	EnableSafDubbo[] value();
}
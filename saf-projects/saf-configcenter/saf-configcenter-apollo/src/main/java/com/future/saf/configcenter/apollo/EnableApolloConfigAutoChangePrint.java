package com.future.saf.configcenter.apollo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ ApolloAutoChangePrintRegistrar.class })
public @interface EnableApolloConfigAutoChangePrint {

}

package com.wiredi.jpa.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface Query {
    String value();

    boolean nativeQuery() default false;

    ParameterStrategy parameterStrategy() default ParameterStrategy.DEFAULT;
}

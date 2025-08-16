package com.wiredi.jpa.annotations;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Repository {
    ParameterStrategy parameterStrategy() default ParameterStrategy.DEFAULT;
}

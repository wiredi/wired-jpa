package com.wiredi.jpa.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface Parameter {
    String value();
}

package com.niit.industrialgasalarmcorporate.infrastructure.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {

    String operation();

    String targetType();
}

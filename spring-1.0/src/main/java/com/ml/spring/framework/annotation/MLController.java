package com.ml.spring.framework.annotation;



import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MLController {

	String value() default "";

}
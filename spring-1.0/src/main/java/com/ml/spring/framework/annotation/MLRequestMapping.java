package com.ml.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName MLRequestMapping
 * @DESC TODO
 * @Author ML
 * @Date 2019/4/25 20:51
 * @Version 1.0
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MLRequestMapping {
    String value() default "";
}

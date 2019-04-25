package com.ml.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName MLService
 * @DESC TODO
 * @Author ML
 * @Date 2019/4/25 20:52
 * @Version 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MLService {
    String value() default "";
}

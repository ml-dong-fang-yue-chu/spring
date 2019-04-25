package com.ml.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName MLAutoWire
 * @DESC TODO
 * @Author ML
 * @Date 2019/4/25 20:52
 * @Version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MLAutoWired {

    String value() default "";
}

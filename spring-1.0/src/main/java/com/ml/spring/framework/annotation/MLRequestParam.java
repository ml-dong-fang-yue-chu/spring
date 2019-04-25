package com.ml.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName MLRequestParam
 * @DESC TODO
 * @Author ML
 * @Date 2019/4/25 20:53
 * @Version 1.0
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MLRequestParam {
    String value() default "";
}

package com.babenko.testkg.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {

    Class<? extends Throwable> expected() default StubThrowable.class;

    class StubThrowable extends Throwable {
        private StubThrowable() {

        }
    }

}

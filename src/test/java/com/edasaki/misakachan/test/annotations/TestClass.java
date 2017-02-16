package com.edasaki.misakachan.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //can use in method only.
public @interface TestClass {

    //should ignore this test?
    public boolean enabled() default true;
    
    // run this test ONLY
    public boolean solo() default false;

}
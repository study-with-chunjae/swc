package net.fullstack7.swc.util;

import net.fullstack7.swc.service.MemberServiceIf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckJwtToken {
    String redirectUri() default "/";
}

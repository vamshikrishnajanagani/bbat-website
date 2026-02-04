package com.telangana.ballbadminton.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for method-level security requiring any of the specified permissions
 * User needs to have at least one of the specified permissions to access the method
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@authorizationService.hasAnyPermission(" +
        "T(com.telangana.ballbadminton.entity.Permission).valueOf(#permissions[0])," +
        "T(com.telangana.ballbadminton.entity.Permission).valueOf(#permissions[1])," +
        "T(com.telangana.ballbadminton.entity.Permission).valueOf(#permissions[2])," +
        "T(com.telangana.ballbadminton.entity.Permission).valueOf(#permissions[3])," +
        "T(com.telangana.ballbadminton.entity.Permission).valueOf(#permissions[4]))")
public @interface RequireAnyPermission {
    /**
     * Array of permissions, user needs at least one
     */
    String[] value();
}
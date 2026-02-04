package com.telangana.ballbadminton.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for method-level security based on roles
 * Requires the user to have a specific role to access the method
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@authorizationService.hasRole(T(com.telangana.ballbadminton.entity.Role).valueOf(#role))")
public @interface RequireRole {
    /**
     * The role required to access the method
     */
    String value();
}
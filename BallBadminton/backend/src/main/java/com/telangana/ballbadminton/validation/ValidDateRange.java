package com.telangana.ballbadminton.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure start date is before end date
 */
@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ValidDateRange.List.class)
public @interface ValidDateRange {
    String message() default "Start date must be before end date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    String startDateField() default "startDate";
    String endDateField() default "endDate";
    
    /**
     * Container annotation for multiple ValidDateRange annotations
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        ValidDateRange[] value();
    }
}
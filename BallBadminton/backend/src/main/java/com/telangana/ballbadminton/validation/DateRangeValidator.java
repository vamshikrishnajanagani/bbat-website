package com.telangana.ballbadminton.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.LocalDate;

/**
 * Validator for ValidDateRange annotation
 * Ensures that start date is before or equal to end date
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startDateField;
    private String endDateField;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startDateField = constraintAnnotation.startDateField();
        this.endDateField = constraintAnnotation.endDateField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }

        try {
            Field startField = value.getClass().getDeclaredField(startDateField);
            Field endField = value.getClass().getDeclaredField(endDateField);
            
            startField.setAccessible(true);
            endField.setAccessible(true);
            
            LocalDate startDate = (LocalDate) startField.get(value);
            LocalDate endDate = (LocalDate) endField.get(value);
            
            if (startDate == null || endDate == null) {
                return true; // Let individual field validation handle null values
            }
            
            return !startDate.isAfter(endDate);
            
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // If we can't access the fields, assume valid and let other validation handle it
            return true;
        }
    }
}
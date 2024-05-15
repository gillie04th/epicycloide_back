package org.epicycloide_back.epicycloide_back.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GreaterThanValidator implements ConstraintValidator<GreaterThan, Double> {

    private double limit;

    @Override
    public void initialize(GreaterThan constraint) {
        limit = constraint.limit();
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        return value > limit;
    }
}

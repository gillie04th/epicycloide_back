package org.epicycloide_back.epicycloide_back.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GreaterThanValidator implements ConstraintValidator<GreaterThan, Double> {

    private double limit;

    @Override
    public void initialize(GreaterThan constraint) {
        limit = constraint.limit();
        System.out.println("test");
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        System.out.println("test");
        return value > limit;
    }
}

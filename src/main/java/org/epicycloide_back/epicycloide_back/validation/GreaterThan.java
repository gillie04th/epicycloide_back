package org.epicycloide_back.epicycloide_back.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;

@Documented
@Target( { FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GreaterThanValidator.class)
public @interface GreaterThan {
    String message() default "La valeur doit être supérieure";
    double limit();

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package org.epicycloide_back.epicycloide_back;

import jakarta.validation.ConstraintViolation;
import org.epicycloide_back.epicycloide_back.model.Epicycloid;
import org.epicycloide_back.epicycloide_back.validation.GreaterThan;
import org.epicycloide_back.epicycloide_back.validation.GreaterThanValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ValidatorTest {

    private GreaterThanValidator validator;



}

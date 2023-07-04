package com.xdev.snaptw.util;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Component
public class ObjectValidator {
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    public <T> void validate (T t){
        final var constraintViolations = validator.validate(t);
        if(constraintViolations.isEmpty()) return;
        final String violationMessages = constraintViolations
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining("\n"));
        throw new IllegalArgumentException(violationMessages);
    }
}

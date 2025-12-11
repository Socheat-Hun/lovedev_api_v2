package com.lovedev.common.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID; /**
 * Validator for UUID format
 */
public class UUIDValidator implements ConstraintValidator<ValidUUID, String> {

    @Override
    public boolean isValid(String uuid, ConstraintValidatorContext context) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return true;
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

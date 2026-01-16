package com.test.demoproductkumamesh.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.OverridesAttribute;
import jakarta.validation.Payload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Valid
@Positive
@Max(value = Integer.MAX_VALUE)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidPositiveIntParam {
    // LINK fieldName TO EVERY INTERNAL CONSTRAINT
//    @OverridesAttribute(constraint = NotNull.class, name = "fieldName")
//    @OverridesAttribute(constraint = Positive.class, name = "fieldName")
//    @OverridesAttribute(constraint = Max.class, name = "fieldName")
    String fieldName() default "field"; // This dummy attribute handles the mapping

    // Override Attributes for NotNull
    @OverridesAttribute(constraint = NotNull.class, name = "message") String notNullMessage() default "{fieldName} is required";

    // Override Attributes for Positive
    @OverridesAttribute(constraint = Positive.class, name = "message") String positiveMessage() default "{fieldName} need to be a positive integer";

    // Override Attributes for Max
    @OverridesAttribute(constraint = Max.class, name = "value") long maxValue() default Integer.MAX_VALUE;
    @OverridesAttribute(constraint = Max.class, name = "message") String maxMessage() default "{fieldName} can't be higher than {maxValue}";

    String message() default "Invalid field";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}

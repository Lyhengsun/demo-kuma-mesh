package com.test.demoproductkumamesh.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.OverridesAttribute;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//@Valid
@Pattern(regexp = "(.*?)")
@Size
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidStringParam {
    String fieldName() default "field";

    // Override Attributes for NotBlank
    @OverridesAttribute(constraint = NotBlank.class, name = "message") String notBlankMessage() default "this field is required";

    // Override Attributes for Size
    @OverridesAttribute(constraint = Size.class, name = "min") int min() default 0;
    @OverridesAttribute(constraint = Size.class, name = "max") int max() default 255;
    @OverridesAttribute(constraint = Size.class, name = "message") String sizeMessage() default "this field need to be between {min} and {max} characters";

    // Override Attributes for Pattern
    @OverridesAttribute(constraint = Pattern.class, name = "regexp") String regexp() default "(.*?)";
    @OverridesAttribute(constraint = Pattern.class, name = "message") String patternMessage() default "Invalid format for this field";

    String message() default "Invalid field";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}

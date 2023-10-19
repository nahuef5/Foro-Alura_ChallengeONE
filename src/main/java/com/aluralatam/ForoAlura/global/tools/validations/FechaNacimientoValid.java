package com.aluralatam.ForoAlura.global.tools.validations;
import jakarta.validation.*;
import java.lang.annotation.*;
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FechaNacimientoValidation.class)
public @interface FechaNacimientoValid{
    String message() default "Debe tener una edad entre {minAge} y {maxAge}.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int minAge() default 18;
    int maxAge() default 65;
}
package bo.edu.ucb.syntax_flavor_backend.validator.email;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreateEmailValidator.class)
@Documented
public @interface ValidCreateEmail {

    String message() default "Invalid email.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
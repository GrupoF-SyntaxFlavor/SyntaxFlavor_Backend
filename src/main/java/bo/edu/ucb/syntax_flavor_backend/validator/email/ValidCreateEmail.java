package bo.edu.ucb.syntax_flavor_backend.validator.email;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreateEmailValidator.class)
@Documented
public @interface ValidCreateEmail {
    // FIXME: Recomendaría mover esta carpeta o bien a util/email/validator o bien a service/email e incporar EmailService en la misma carpeta

    String message() default "Invalid email.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

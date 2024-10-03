package bo.edu.ucb.syntax_flavor_backend.validator.account;


import bo.edu.ucb.syntax_flavor_backend.user.bl.UserBL;
import bo.edu.ucb.syntax_flavor_backend.user.dto.UserDTO;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;


public class CreateEmailValidator implements ConstraintValidator<ValidCreateEmail, String> {

    @Autowired
    private UserBL userBL;

    @Override
    public void initialize(ValidCreateEmail constraintAnnotation) {
        // TODO Auto-generated method stub
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        try {
            UserDTO user = userBL.getUserByEmail(email);
            if (user != null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Email already used in other account")
                        .addConstraintViolation();
                return false;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Excepción inesperada durante la llamada a isValid: " + e);
            e.printStackTrace();
            throw new ValidationException("Unexpected exception during isValid call.", e);
        }
    }
}


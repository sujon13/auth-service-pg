package example.demo.signup.validator;


import example.demo.signup.annotation.PasswordMatcher;
import example.demo.signup.model.SignupRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatcherValidator implements ConstraintValidator<PasswordMatcher, SignupRequest> {

    @Override
    public void initialize(PasswordMatcher constraintAnnotation) {
    }

    @Override
    public boolean isValid(SignupRequest signupRequest, ConstraintValidatorContext context) {
        return signupRequest.getRawPassword().equals(signupRequest.getReTypeRawPassword());
    }
}
package by.k19.usercore.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

public class UserErrorBuilder {

    public static UserError fromBindingErrors(Errors errors) {
        UserError userError = new UserError("Validation error. Detected " + errors.getErrorCount() + " errors");
        for (ObjectError objectError : errors.getAllErrors()) {
            userError.addError(objectError.getDefaultMessage());
        }
        return userError;
    }
}

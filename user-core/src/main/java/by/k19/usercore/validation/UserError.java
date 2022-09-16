package by.k19.usercore.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class UserError {
    private String errorMessage;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<String> errors = new ArrayList<>();

    public UserError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void addError(String error) {
        errors.add(error);
    }
}

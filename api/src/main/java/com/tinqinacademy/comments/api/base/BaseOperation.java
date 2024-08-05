package com.tinqinacademy.comments.api.base;

import com.tinqinacademy.comments.api.errors.ErrorMapper;
import com.tinqinacademy.comments.api.errors.Errors;
import com.tinqinacademy.comments.api.messages.InvalidInputException;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import jakarta.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@Setter
public abstract class BaseOperation {

    protected final Validator validator;
    protected final ConversionService conversionService;
    protected final ErrorMapper errorMapper;

    public <T extends OperationInput> void validate ( T input ) {
        Set<ConstraintViolation<T>> violations = validator.validate(input);

        if (!violations.isEmpty()) {
            List<Errors> errorList = new ArrayList<>();
            violations.forEach(violation -> {
                Errors error = Errors.builder()
                        .message(violation.getMessage())
                        .field(violation.getPropertyPath().toString())
                        .build();
                errorList.add(error);
            });
            throw new InvalidInputException(errorMapper.mapErrors(errorList, HttpStatus.BAD_REQUEST));
        }
    }
}

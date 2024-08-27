package com.tinqinacademy.comments.api.errors;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Errors {

    private List<Error> errors;

    private String message;
    private String field;
    private int httpStatusCode;


    public Errors ( String unexpectedError ) {
        this.message = unexpectedError;
        this.errors = List.of();
    }

    public Errors(String message, int httpStatusCode) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }


    public String getMessage() {
        if (errors == null || errors.isEmpty()) {
            return "";
        }
        return errors.stream()
                .map(Error::getMessage)
                .collect(Collectors.joining(", "));
    }

    public static Errors of(String message) {
        return new Errors(message);
    }
}

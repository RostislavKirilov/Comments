package com.tinqinacademy.comments.api.messages;

import com.tinqinacademy.comments.api.errors.ErrorOutput;

public class InvalidInputException extends RuntimeException{

    public InvalidInputException(ErrorOutput errorOutput) {
        super(errorOutput.getMessage());
    }
}

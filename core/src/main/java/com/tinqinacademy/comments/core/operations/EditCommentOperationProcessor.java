package com.tinqinacademy.comments.core.operations;

import com.tinqinacademy.comments.api.base.BaseOperation;
import com.tinqinacademy.comments.api.contracts.operations.editcomment.EditCommentInput;
import com.tinqinacademy.comments.api.contracts.operations.editcomment.EditCommentOperation;
import com.tinqinacademy.comments.api.contracts.operations.editcomment.EditCommentOutput;
import com.tinqinacademy.comments.api.errors.Error;
import com.tinqinacademy.comments.api.errors.ErrorMapper;
import com.tinqinacademy.comments.api.errors.ErrorOutput;
import com.tinqinacademy.comments.api.errors.Errors;
import com.tinqinacademy.comments.api.messages.ExceptionMessages;
import com.tinqinacademy.comments.persistence.entities.CommentEntity;
import com.tinqinacademy.comments.persistence.repository.CommentRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class EditCommentOperationProcessor extends BaseOperation implements EditCommentOperation {

    private final CommentRepository commentRepository;
    private final ExceptionMessages exceptionMessages;

    public EditCommentOperationProcessor( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, CommentRepository commentRepository, ExceptionMessages exceptionMessages ) {
        super(validator, conversionService, errorMapper);
        this.commentRepository = commentRepository;
        this.exceptionMessages = exceptionMessages;
    }

    @Override
    public Either<Errors, EditCommentOutput> process(EditCommentInput input) {
        return Try.of(() -> {
                    log.info("Start editing comment with ID: {}", input.getCommentId());

                    // Validate the input
                    validate(input);

                    // Find the comment by its ID
                    CommentEntity commentEntity = commentRepository.findById(UUID.fromString(input.getCommentId()))
                            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

                    // Update the content if provided
                    if (input.getContent() != null && !input.getContent().isEmpty()) {
                        commentEntity.setComment(input.getContent());
                    }

                    // Update the last edit time
                    commentEntity.setLastEditTime(LocalDateTime.now());

                    // Save the updated comment
                    commentRepository.save(commentEntity);

                    // Prepare the output
                    EditCommentOutput output = EditCommentOutput.builder()
                            .commentId(commentEntity.getId().toString())
                            .content(commentEntity.getComment())
                            .roomId(commentEntity.getRoomId().toString())
                            .build();

                    log.info("End editing comment with ID: {}", input.getCommentId());
                    return output;
                })
                .toEither()
                .mapLeft(this::handleErrors);
    }

    private Errors handleErrors(Throwable throwable) {
        ErrorOutput errorOutput = matchError(throwable);
        return Errors.builder()
                .errors(List.of(new Error(errorOutput.getMessage())))
                .message(errorOutput.getMessage())
                .build();
    }

    private ErrorOutput matchError(Throwable throwable) {
        if (throwable instanceof IllegalArgumentException) {
            String message = throwable.getMessage();
            if (message != null && message.contains("Comment not found")) {
                return new ErrorOutput(List.of(new Errors(message)), HttpStatus.NOT_FOUND);
            }
            return new ErrorOutput(List.of(new Errors(message)), HttpStatus.BAD_REQUEST);
        }
        return new ErrorOutput(List.of(new Errors("An unexpected error occurred")), HttpStatus.INTERNAL_SERVER_ERROR);
    }





    private void validate(EditCommentInput input) {
        if (input.getContent() != null && input.getContent().length() > 1000) {
            throw new IllegalArgumentException("Content must be less than or equal to 1000 characters");
        }
    }

}

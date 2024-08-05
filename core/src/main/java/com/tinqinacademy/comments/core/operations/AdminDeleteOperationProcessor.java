package com.tinqinacademy.comments.core.operations;

import com.tinqinacademy.comments.api.base.BaseOperation;
import com.tinqinacademy.comments.api.contracts.operations.admindelete.AdminDeleteInput;
import com.tinqinacademy.comments.api.contracts.operations.admindelete.AdminDeleteOperation;
import com.tinqinacademy.comments.api.contracts.operations.admindelete.AdminDeleteOutput;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AdminDeleteOperationProcessor extends BaseOperation implements AdminDeleteOperation {

    private final CommentRepository commentRepository;
    private final ExceptionMessages exceptionMessages;

    public AdminDeleteOperationProcessor(Validator validator, ConversionService conversionService, ErrorMapper errorMapper, CommentRepository commentRepository, ExceptionMessages exceptionMessages) {
        super(validator, conversionService, errorMapper);
        this.commentRepository = commentRepository;
        this.exceptionMessages = exceptionMessages;
    }

    @Override
    public Either<Errors, AdminDeleteOutput> process(AdminDeleteInput input) {
        return Try.of(() -> {
                    log.info("Start deleting comment with input: {}", input);
                    validate(input);

                    UUID commentUUID = UUID.fromString(input.getCommentId());
                    Optional<CommentEntity> commentOptional = commentRepository.findById(commentUUID);

                    if (commentOptional.isEmpty()) {
                        throw new IllegalArgumentException(exceptionMessages.getCommentNotFound());
                    }

                    CommentEntity comment = commentOptional.get();
                    if (!comment.getRoomId().equals(input.getRoomId())) {
                        throw new IllegalArgumentException(exceptionMessages.getRoomNotFound());
                    }

                    commentRepository.delete(comment);
                    AdminDeleteOutput output = AdminDeleteOutput.builder()
                            .commentId(input.getCommentId())
                            .message("Comment successfully deleted!")
                            .build();

                    log.info("End deleting comment with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(this::handleErrors);
    }

    private Errors handleErrors(Throwable throwable) {
        ErrorOutput errorOutput = matchError(throwable);
        return new Errors(List.of(Error.builder()
                .message(errorOutput.getMessage())
                .build()).toString());
    }

    private ErrorOutput matchError(Throwable throwable) {
        return io.vavr.API.Match(throwable).of(
                io.vavr.API.Case(io.vavr.API.$(IllegalArgumentException.class::isInstance),
                        new ErrorOutput(List.of(Errors.of(exceptionMessages.getCommentNotFound())), HttpStatus.BAD_REQUEST)),
                io.vavr.API.Case(io.vavr.API.$(),
                        new ErrorOutput(List.of(Errors.of(exceptionMessages.getRoomNotFound())), HttpStatus.INTERNAL_SERVER_ERROR))
        );
    }
}

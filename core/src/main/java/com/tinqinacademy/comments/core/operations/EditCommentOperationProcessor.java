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
                    log.info("Start editing comment with input: {}", input);
                    validate(input);

                    CommentEntity commentEntity = commentRepository.findById(UUID.fromString(input.getCommentId()))
                            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

                    commentEntity.setComment(input.getContent());
                    commentEntity.setRoomId(input.getRoomId());
                    commentEntity.setLastEditTime(LocalDateTime.now());

                    commentRepository.save(commentEntity);

                    EditCommentOutput output = EditCommentOutput.builder()
                            .commentId(input.getCommentId())
                            .content(commentEntity.getComment())
                            .roomId(commentEntity.getRoomId())
                            .build();

                    log.info("End editing comment with output: {}", output);
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
        return io.vavr.API.Match(throwable).of(
                io.vavr.API.Case(io.vavr.API.$(IllegalArgumentException.class::isInstance),
                        new ErrorOutput(List.of(Errors.of(exceptionMessages.getCommentNotFound())), HttpStatus.BAD_REQUEST)),
                io.vavr.API.Case(io.vavr.API.$(),
                        new ErrorOutput(List.of(Errors.of(exceptionMessages.getRoomNotFound())), HttpStatus.INTERNAL_SERVER_ERROR))
        );
    }
}

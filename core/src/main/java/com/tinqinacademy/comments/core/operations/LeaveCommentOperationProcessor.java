package com.tinqinacademy.comments.core.operations;

import com.tinqinacademy.comments.api.base.BaseOperation;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentInput;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentOperation;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentOutput;
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

@Service
@Slf4j
public class LeaveCommentOperationProcessor extends BaseOperation implements LeaveCommentOperation {

    private final CommentRepository commentRepository;
    private final ExceptionMessages exceptionMessages;

    public LeaveCommentOperationProcessor( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, CommentRepository commentRepository, ExceptionMessages exceptionMessages ) {
        super(validator, conversionService, errorMapper);
        this.commentRepository = commentRepository;
        this.exceptionMessages = exceptionMessages;
    }

    @Override
    public Either<Errors, LeaveCommentOutput> process(LeaveCommentInput input) {
        return Try.of(() -> {
                    log.info("Start leaving comment with input: {}", input);
                    validate(input);

                    CommentEntity commentEntity = CommentEntity.builder()
                            .roomId(input.getRoomId())
                            .firstName(input.getFirstName())
                            .lastName(input.getLastName())
                            .comment(input.getComment())
                            .publishedDate(LocalDateTime.now())
                            .build();

                    commentRepository.save(commentEntity);

                    LeaveCommentOutput output = LeaveCommentOutput.builder()
                            .roomId(commentEntity.getRoomId())
                            .comment(commentEntity.getComment())
                            .publishedDate(commentEntity.getPublishedDate())
                            .build();

                    log.info("End leaving comment with output: {}", output);
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

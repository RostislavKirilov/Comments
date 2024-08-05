package com.tinqinacademy.comments.core.operations;

import com.tinqinacademy.comments.api.base.BaseOperation;
import com.tinqinacademy.comments.api.contracts.operations.getallcomments.GetCommentsInput;
import com.tinqinacademy.comments.api.contracts.operations.getallcomments.GetCommentsOperation;
import com.tinqinacademy.comments.api.contracts.operations.getallcomments.GetCommentsOutput;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class GetCommentsOperationProcessor extends BaseOperation implements GetCommentsOperation {

    private final CommentRepository commentRepository;
    private final ExceptionMessages exceptionMessages;

    public GetCommentsOperationProcessor(Validator validator, ConversionService conversionService,
                                         ErrorMapper errorMapper, CommentRepository commentRepository,
                                         ExceptionMessages exceptionMessages) {
        super(validator, conversionService, errorMapper);
        this.commentRepository = commentRepository;
        this.exceptionMessages = exceptionMessages;
    }

    @Override
    public Either<Errors, GetCommentsOutput> process(GetCommentsInput input) {
        return Try.of(() -> {
                    log.info("Start getting comments with input: {}", input);
                    validate(input);

                    List<CommentEntity> comments = commentRepository.findByRoomId(input.getRoomId());

                    if (comments.isEmpty()) {
                        throw new IllegalArgumentException("No comments found for the given room ID");
                    }

                    List<GetCommentsOutput> output = comments.stream()
                            .map(comment -> GetCommentsOutput.builder()
                                    .roomId(comment.getRoomId())
                                    .comment(comment.getComment())
                                    .publishDate(comment.getPublishedDate())
                                    .lastEditTime(comment.getLastEditTime())
                                    .editedBy(comment.getEditedBy())
                                    .name(comment.getFirstName() + " " + comment.getLastName())
                                    .build())
                            .collect(Collectors.toList());

                    log.info("End getting comments with output: {}", output);
                    return GetCommentsOutput.builder()
                            .comments(output)
                            .build();
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

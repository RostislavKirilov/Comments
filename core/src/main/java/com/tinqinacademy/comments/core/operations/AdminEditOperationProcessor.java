package com.tinqinacademy.comments.core.operations;
import com.tinqinacademy.comments.api.base.BaseOperation;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditInput;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditOperation;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditOutput;
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

@Slf4j
@Service
public class AdminEditOperationProcessor extends BaseOperation implements AdminEditOperation {

    private final CommentRepository commentRepository;
    private final ExceptionMessages exceptionMessages;

    public AdminEditOperationProcessor( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, CommentRepository commentRepository, ExceptionMessages exceptionMessages ) {
        super(validator, conversionService, errorMapper);
        this.commentRepository = commentRepository;
        this.exceptionMessages = exceptionMessages;
    }

    @Override
    public Either<Errors, AdminEditOutput> process(AdminEditInput input) {
        return Try.of(() -> {
                    log.info("Start editing comment with input: {}", input);
                    validate(input);

                    UUID commentUUID = UUID.fromString(input.getCommentId());
                    Optional<CommentEntity> commentOptional = commentRepository.findById(commentUUID);

                    if (commentOptional.isEmpty()) {
                        throw new IllegalArgumentException("Comment not found!");
                    }

                    CommentEntity comment = commentOptional.get();
                    comment.setRoomId(input.getRoomNo());
                    comment.setFirstName(input.getFirstName());
                    comment.setLastName(input.getLastName());
                    comment.setContent(input.getContent());
                    commentRepository.save(comment);

                    AdminEditOutput output = AdminEditOutput.builder()
                            .commentId(input.getCommentId())
                            .message("Comment successfully edited!")
                            .build();

                    log.info("End editing comment with output: {}", output);
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

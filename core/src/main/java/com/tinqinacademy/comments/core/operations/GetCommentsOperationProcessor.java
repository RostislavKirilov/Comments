package com.tinqinacademy.comments.core.operations;

import com.tinqinacademy.comments.api.base.BaseOperation;
import com.tinqinacademy.comments.api.contracts.operations.getallcomments.CommentOutput;
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
import java.util.UUID;
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
                    // Валидиране на входните данни
                    validate(input);

                    List<CommentEntity> commentEntities = commentRepository.findByRoomId(UUID.fromString(input.getRoomId()));

                    List<CommentOutput> comments = commentEntities.stream()
                            .map(entity -> CommentOutput.builder()
                                    .roomId(entity.getRoomId().toString())
                                    .comment(entity.getComment()) // Използвайте content ако е нужно
                                    .publishDate(entity.getPublishedDate())
                                    .lastEditTime(entity.getLastEditTime())
                                    .editedBy(entity.getEditedBy())
                                    .name(entity.getFirstName() + " " + entity.getLastName()) // Комбинирайте първо и последно име
                                    .build())
                            .collect(Collectors.toList());

                    return GetCommentsOutput.builder()
                            .comments(comments)
                            .build();
                })
                .toEither()
                .mapLeft(this::handleErrors);
    }

    private Errors handleErrors(Throwable throwable) {
        // Обработка на грешки с ErrorMapper
        return errorMapper.map(throwable, HttpStatus.BAD_REQUEST).getErrors().get(0);
    }
}
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
        UUID commentIdUUID;

        try {
            commentIdUUID = UUID.fromString(input.getCommentId());
        } catch (IllegalArgumentException e) {
            return Either.left(new Errors("Invalid ID format!", HttpStatus.BAD_REQUEST.value()));
        }

        Optional<CommentEntity> commentEntityOptional = commentRepository.findById(commentIdUUID);

        if (commentEntityOptional.isEmpty()) {
            return Either.left(new Errors("Comment not found!", HttpStatus.NOT_FOUND.value()));
        }

        CommentEntity commentEntity = commentEntityOptional.get();
        commentRepository.delete(commentEntity);

        AdminDeleteOutput output = AdminDeleteOutput.builder()
                .commentId(input.getCommentId())
                .message("Comment successfully deleted!")
                .build();

        return Either.right(output);
    }

}
package com.tinqinacademy.comments.core.operations;
import com.tinqinacademy.comments.api.base.BaseOperation;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditInput;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditOperation;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditOutput;
import com.tinqinacademy.comments.api.errors.ErrorMapper;
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

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AdminEditOperationProcessor extends BaseOperation implements AdminEditOperation {

    private final CommentRepository commentRepository;
    private final ExceptionMessages exceptionMessages;
    private final ErrorMapper errorMapper;

    public AdminEditOperationProcessor( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, CommentRepository commentRepository, ExceptionMessages exceptionMessages, ErrorMapper errorMapper1 ) {
        super(validator, conversionService, errorMapper);
        this.commentRepository = commentRepository;
        this.exceptionMessages = exceptionMessages;
        this.errorMapper = errorMapper1;
    }

    @Override
    public Either<Errors, AdminEditOutput> process(AdminEditInput input) {
        try {
            // Validate input (ensure 'commentId' is valid UUID)
            validate(input);

            UUID commentId = UUID.fromString(input.getCommentId());

            // Find the comment
            Optional<CommentEntity> optionalComment = commentRepository.findById(commentId);
            if (optionalComment.isEmpty()) {
                throw new IllegalArgumentException("Comment not found.");
            }

            // Update the comment
            CommentEntity commentEntity = optionalComment.get();
            commentEntity.setContent(input.getContent());
            // Other updates if needed
            commentRepository.save(commentEntity);

            return Either.right(AdminEditOutput.builder()
                    .commentId(input.getCommentId())
                    .message("Comment updated successfully.")
                    .build());
        } catch (IllegalArgumentException e) {
            // Handle errors
            return Either.left(new Errors(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            // General error handling
            return Either.left(new Errors("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }


    private Errors handleErrors(Throwable throwable) {
        // Обработка на грешки с ErrorMapper
        return errorMapper.map(throwable, HttpStatus.BAD_REQUEST).getErrors().get(0);
    }
}

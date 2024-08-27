package com.tinqinacademy.comments.rest.controllers;

import com.tinqinacademy.comments.api.contracts.operations.admindelete.AdminDeleteInput;
import com.tinqinacademy.comments.api.contracts.operations.admindelete.AdminDeleteOutput;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditInput;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditOutput;
import com.tinqinacademy.comments.api.contracts.routes.RestApiRoutesComments;
import com.tinqinacademy.comments.api.errors.Errors;
import com.tinqinacademy.comments.core.operations.AdminDeleteOperationProcessor;
import com.tinqinacademy.comments.core.operations.AdminEditOperationProcessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@ControllerAdvice
@Slf4j
public class AdminController {

   private final AdminDeleteOperationProcessor adminDeleteOperationProcessor;
   private final AdminEditOperationProcessor adminEditOperationProcessor;


    @PatchMapping(RestApiRoutesComments.ADMIN_EDIT)
    public ResponseEntity<?> editComment(@PathVariable("commentId") String commentId, @RequestBody AdminEditInput input) {
        input.setCommentId(commentId);

        Either<Errors, AdminEditOutput> result = adminEditOperationProcessor.process(input);

        return result.fold(
                error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error),
                ResponseEntity::ok
        );
    }

    @DeleteMapping(RestApiRoutesComments.ADMIN_DELETE)
    @Operation(summary = "Admin can delete any comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment successfully deleted!"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format!"),
            @ApiResponse(responseCode = "404", description = "Comment not found!"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AdminDeleteOutput> deleteComment(@PathVariable String commentId) {
        log.info("Received request to delete comment with ID: {}", commentId);

        AdminDeleteInput input = AdminDeleteInput.builder()
                .commentId(commentId)
                .build();

        Either<Errors, AdminDeleteOutput> result = adminDeleteOperationProcessor.process(input);

        return result.fold(
                error -> {
                    log.error("Error during delete operation: {}", error.getMessage());
                    HttpStatus status = error.getHttpStatusCode() == HttpStatus.NOT_FOUND.value() ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
                    return ResponseEntity.status(status)
                            .body(new AdminDeleteOutput(commentId, error.getMessage()));
                },
                response -> {
                    log.info("Successfully deleted comment with ID: {}", commentId);
                    return ResponseEntity.ok(response);
                }
        );
    }

}

package com.tinqinacademy.comments.rest.controllers;
import com.tinqinacademy.comments.api.contracts.operations.editcomment.EditCommentInput;
import com.tinqinacademy.comments.api.contracts.operations.editcomment.EditCommentOutput;
import com.tinqinacademy.comments.api.contracts.operations.getallcomments.GetCommentsInput;
import com.tinqinacademy.comments.api.contracts.operations.getallcomments.GetCommentsOutput;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentInput;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentOutput;
import com.tinqinacademy.comments.api.contracts.routes.RestApiRoutesComments;
import com.tinqinacademy.comments.api.errors.Errors;
import com.tinqinacademy.comments.core.operations.EditCommentOperationProcessor;
import com.tinqinacademy.comments.core.operations.GetCommentsOperationProcessor;
import com.tinqinacademy.comments.api.messages.ExceptionMessages;
import com.tinqinacademy.comments.core.operations.LeaveCommentOperationProcessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@Slf4j
public class HotelController {

    private final LeaveCommentOperationProcessor leaveCommentOperationProcessor;
    private final GetCommentsOperationProcessor getCommentsOperationProcessor;
    private final EditCommentOperationProcessor editCommentOperationProcessor;
    private final ExceptionMessages exceptionMessages;

    private final Counter leaveCommentCounter;

    @Autowired
    public HotelController(
            LeaveCommentOperationProcessor leaveCommentOperationProcessor,
            GetCommentsOperationProcessor getCommentsOperationProcessor,
            EditCommentOperationProcessor editCommentOperationProcessor,
            ExceptionMessages exceptionMessages,
            MeterRegistry meterRegistry) {
        this.leaveCommentOperationProcessor = leaveCommentOperationProcessor;
        this.getCommentsOperationProcessor = getCommentsOperationProcessor;
        this.editCommentOperationProcessor = editCommentOperationProcessor;
        this.exceptionMessages = exceptionMessages;
        this.leaveCommentCounter = meterRegistry.counter("custom_leave_comment_counter"); // Инициализиране на брояча
    }



    @GetMapping(RestApiRoutesComments.GET_COMMENTS)
    @Operation(summary = "Get all comments for a certain room.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments successfully retrieved!"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Room not found!"),
    })
    public ResponseEntity<?> getAllComments ( @PathVariable String roomId ) {
        GetCommentsInput input = GetCommentsInput.builder()
                .roomId(roomId)
                .build();

        log.info("Received request to get comments for room ID: {}", roomId);

        Either<Errors, GetCommentsOutput> result = getCommentsOperationProcessor.process(input);

        return result.fold(
                error -> {
                    log.warn("Get comments failed: {}", error.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                },
                response -> {
                    log.info("Get comments successful for room ID: {}", roomId);
                    return ResponseEntity.ok(response);
                }
        );
    }

    private HttpStatus determineHttpStatus ( Errors errors ) {
        if (errors.getMessage().contains("No comments found")) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }

//    @PostMapping(RestApiRoutesComments.LEAVE_COMMENT)
//    @Operation(summary = "Leave a comment for a certain room.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Comment successfully added!"),
//            @ApiResponse(responseCode = "400", description = "Invalid input data"),
//            @ApiResponse(responseCode = "404", description = "Room not found!"),
//    })
//    public ResponseEntity<?> leaveComment(@RequestBody @Valid LeaveCommentInput leaveCommentInput) {
//        Either<Errors, LeaveCommentOutput> result = leaveCommentOperationProcessor.process(leaveCommentInput);
//
//        if (result.isRight()) {
//            LeaveCommentOutput output = result.get();
//            return ResponseEntity.ok(output);
//        } else {
//            Errors errors = result.getLeft();
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(errors);
//        }
//    }


    @PostMapping(RestApiRoutesComments.LEAVE_COMMENT)
    @Operation(summary = "Leave a comment for a certain room.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment successfully added!"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Room not found!"),
    })
    public ResponseEntity<?> leaveComment(@RequestBody @Valid LeaveCommentInput leaveCommentInput) {
        leaveCommentCounter.increment();

        Either<Errors, LeaveCommentOutput> result = leaveCommentOperationProcessor.process(leaveCommentInput);

        if (result.isRight()) {
            LeaveCommentOutput output = result.get();
            return ResponseEntity.ok(output);
        } else {
            Errors errors = result.getLeft();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors);
        }
    }

    @PatchMapping(RestApiRoutesComments.USER_EDIT)
    @Operation(summary = "Edit a comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment successfully edited!"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Comment not found!")
    })
    public ResponseEntity<?> editComment(
            @PathVariable String commentId,
            @Valid @RequestBody EditCommentInput editCommentInput) {

        log.info("Received request to edit comment with ID: {}", commentId);

        // Set the comment ID in the input object
        editCommentInput.setCommentId(commentId);

        Either<Errors, EditCommentOutput> result = editCommentOperationProcessor.process(editCommentInput);

        if (result.isRight()) {
            EditCommentOutput output = result.get();
            return ResponseEntity.ok(output);
        } else {
            Errors errors = result.getLeft();
            HttpStatus status = errors.getMessage().contains("Comment not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(errors);
        }
    }
    // Remove or replace your current /metrics endpoint with:
    @GetMapping("/actuator/metrics")
    public ResponseEntity<String> getMetrics() {
        // The Actuator endpoint will handle this for you
        return ResponseEntity.ok("Metrics are exposed by Actuator.");
    }

}
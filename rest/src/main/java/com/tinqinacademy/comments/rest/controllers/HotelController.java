package com.tinqinacademy.comments.rest.controllers;
import com.tinqinacademy.comments.api.contracts.operations.editcomment.EditCommentInput;
import com.tinqinacademy.comments.api.contracts.operations.editcomment.EditCommentOutput;
import com.tinqinacademy.comments.api.contracts.operations.getallcomments.GetCommentsInput;
import com.tinqinacademy.comments.api.contracts.operations.getallcomments.GetCommentsOutput;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentInput;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentOperation;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentOutput;
import com.tinqinacademy.comments.api.contracts.RestApiRoutes;
import com.tinqinacademy.comments.api.errors.Errors;
import com.tinqinacademy.comments.core.operations.EditCommentOperationProcessor;
import com.tinqinacademy.comments.core.operations.GetCommentsOperationProcessor;
import com.tinqinacademy.comments.api.messages.ExceptionMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class HotelController {

    private static final Logger log = LoggerFactory.getLogger(HotelController.class);
    private final LeaveCommentOperation leaveCommentOperation;
    private final GetCommentsOperationProcessor getCommentsOperationProcessor;
    private final EditCommentOperationProcessor editCommentOperationProcessor;
    private final ExceptionMessages exceptionMessages;

    @GetMapping(RestApiRoutes.GET_COMMENTS)
    @Operation(summary = "Get all comments for a certain room.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments successfully retrieved!"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Room not found!"),
    })
    public ResponseEntity<?> getAllComments(@RequestParam String roomId) {
        GetCommentsInput input = GetCommentsInput.builder()
                .roomId(roomId)
                .build();

        Either<Errors, GetCommentsOutput> result = getCommentsOperationProcessor.process(input);

        if (result.isRight()) {
            GetCommentsOutput output = result.get();
            return ResponseEntity.ok(output.getComments());
        } else {
            Errors errors = result.getLeft();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors);
        }
    }

    @PostMapping(RestApiRoutes.LEAVE_COMMENT)
    @Operation(summary = "Leave a comment for a certain room.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment successfully added!"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Room not found!"),
    })
    public ResponseEntity<?> leaveComment(@RequestBody @Valid LeaveCommentInput leaveCommentInput) {
        Either<Errors, LeaveCommentOutput> result = leaveCommentOperation.process(leaveCommentInput);

        if (result.isRight()) {
            LeaveCommentOutput output = result.get();
            return ResponseEntity.ok(output);
        } else {
            Errors errors = result.getLeft();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors);
        }
    }

    @PatchMapping(RestApiRoutes.USER_EDIT)
    @Operation(summary = "Edit comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment successfully edited!"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Comment not found!"),
    })
    public ResponseEntity<?> editComment(
            @PathVariable String commentId, @Valid @RequestBody EditCommentInput editCommentInput) {

        log.info("Editing comment with ID: {} and input: {}", commentId, editCommentInput);

        EditCommentInput input = EditCommentInput.builder()
                .content(editCommentInput.getContent())
                .roomId(editCommentInput.getRoomId())
                .build();

        Either<Errors, EditCommentOutput> result = editCommentOperationProcessor.process(input);

        if (result.isRight()) {
            EditCommentOutput output = result.get();
            return ResponseEntity.ok(output);
        } else {
            Errors errors = result.getLeft();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors);
        }
    }
}
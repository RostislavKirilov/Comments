package com.tinqinacademy.comments.rest.controllers;

import com.tinqinacademy.comments.api.contracts.operations.admindelete.AdminDeleteInput;
import com.tinqinacademy.comments.api.contracts.operations.admindelete.AdminDeleteOperation;
import com.tinqinacademy.comments.api.contracts.operations.admindelete.AdminDeleteOutput;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditInput;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditOperation;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditOutput;
import com.tinqinacademy.comments.api.contracts.RestApiRoutes;
import com.tinqinacademy.comments.api.errors.Errors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@ControllerAdvice
public class AdminController {

   private final AdminDeleteOperation adminDeleteOperation;
   private final AdminEditOperation adminEditOperation;


    @PatchMapping(RestApiRoutes.ADMIN_EDIT)
    @Operation(summary = "Admin can edit any comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment successfully edited!"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Comment not found!"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AdminEditOutput> adminEdit(@RequestBody AdminEditInput adminEditInput) {
        Either<Errors, AdminEditOutput> result = adminEditOperation.process(adminEditInput);

        if (result.isRight()) {
            AdminEditOutput output = result.get();
            return ResponseEntity.ok(output);
        } else {
            Errors errors = result.getLeft();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AdminEditOutput.builder()
                            .commentId(adminEditInput.getCommentId())
                            .message(errors.getMessage())
                            .build());
        }
    }

    @DeleteMapping(RestApiRoutes.ADMIN_DELETE)
    @Operation(summary = "Admin can delete any comment for a certain room.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment successfully deleted!"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Comment not found!"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AdminDeleteOutput> adminDelete(
            @RequestParam String commentId,
            @RequestParam String roomId) {
        AdminDeleteInput input = new AdminDeleteInput(commentId, roomId);
        Either<Errors, AdminDeleteOutput> result = adminDeleteOperation.process(input);

        if (result.isRight()) {
            AdminDeleteOutput output = result.get();
            return ResponseEntity.ok(output);
        } else {
            Errors errors = result.getLeft();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AdminDeleteOutput(commentId, errors.getMessage()));
        }
    }
}

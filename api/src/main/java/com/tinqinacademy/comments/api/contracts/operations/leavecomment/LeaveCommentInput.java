package com.tinqinacademy.comments.api.contracts.operations.leavecomment;

import com.tinqinacademy.comments.api.base.OperationInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LeaveCommentInput implements OperationInput {

    @NotBlank(message = "Room ID must not be blank!")
    private String roomId;
    @NotBlank(message = "Please, enter your first name!")
    @Schema(example = "Jon")
    private String firstName;
    @NotBlank(message = "Please, enter your last name!")
    @Schema(example = "Doe")
    private String lastName;
    @NotBlank(message = "Please, enter your comment!")
    private String comment;




}

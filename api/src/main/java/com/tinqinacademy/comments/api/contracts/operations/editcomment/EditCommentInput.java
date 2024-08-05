package com.tinqinacademy.comments.api.contracts.operations.editcomment;

import com.tinqinacademy.comments.api.base.OperationInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EditCommentInput implements OperationInput {

    @NotBlank
    @Size(max = 100, message = "Content must be less than or equal to 1000 characters")
    private String content;

    @NotBlank(message = "Room ID cannot be blank")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Room ID must be alphanumeric")
    @Schema(example = "101A")
    private String roomId;

    private String commentId;

}

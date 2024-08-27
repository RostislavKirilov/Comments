package com.tinqinacademy.comments.api.contracts.operations.editcomment;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Size(max = 1000, message = "Content must be less than or equal to 1000 characters")
    private String content;

    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Room ID must be alphanumeric")
    @JsonIgnore
    private String roomId;
    @JsonIgnore
    private String commentId;

}

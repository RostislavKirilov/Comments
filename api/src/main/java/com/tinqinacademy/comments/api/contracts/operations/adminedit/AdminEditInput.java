package com.tinqinacademy.comments.api.contracts.operations.adminedit;

import com.tinqinacademy.comments.api.base.OperationInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdminEditInput implements OperationInput {

    @Schema(example = "418")
    private String commentId;
    @NotBlank(message = "Fill the room number.")
    @Schema(example = "101A")
    private String roomNo;
    @Schema(example = "John")
    private String firstName;
    @Schema(example = "Doe")
    private String lastName;
    @NotBlank(message = "Enter some content.")
    private String content;
}

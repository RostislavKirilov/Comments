package com.tinqinacademy.comments.api.contracts.operations.adminedit;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Schema(example = "101A")
    @JsonIgnore
    private String roomNo;

    @Schema(example = "John")
    @JsonIgnore
    private String firstName;

    @Schema(example = "Doe")
    @JsonIgnore
    private String lastName;

    @NotBlank(message = "Enter some content.")
    private String content;
}

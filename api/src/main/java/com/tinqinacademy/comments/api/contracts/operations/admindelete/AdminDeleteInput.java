package com.tinqinacademy.comments.api.contracts.operations.admindelete;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.comments.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdminDeleteInput implements OperationInput {

    @JsonIgnore
    private String roomId;
    @NotBlank(message = "Fill the commend ID")
    private String commentId;
}

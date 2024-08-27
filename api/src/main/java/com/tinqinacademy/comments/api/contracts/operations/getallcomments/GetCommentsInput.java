package com.tinqinacademy.comments.api.contracts.operations.getallcomments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.comments.api.base.OperationInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCommentsInput implements OperationInput {

    @JsonIgnore
    private String roomId;
    private String comment;

}

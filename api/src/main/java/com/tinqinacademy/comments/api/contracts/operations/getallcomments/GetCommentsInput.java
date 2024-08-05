package com.tinqinacademy.comments.api.contracts.operations.getallcomments;

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

    @NotBlank(message = "Room ID must not be blank!")
    @Schema(example = "101A")
    private String roomId;

    @NotBlank(message = "Please, enter your comment!")
    private String comment;

}

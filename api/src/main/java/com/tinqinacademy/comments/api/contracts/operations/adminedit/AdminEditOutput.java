package com.tinqinacademy.comments.api.contracts.operations.adminedit;

import com.tinqinacademy.comments.api.base.OperationOutput;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdminEditOutput implements OperationOutput {

    private String commentId;
    private String message;
}

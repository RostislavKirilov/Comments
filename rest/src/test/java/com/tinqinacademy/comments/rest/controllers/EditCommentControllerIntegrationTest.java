package com.tinqinacademy.comments.rest.controllers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.comments.api.contracts.routes.RestApiRoutesComments;
import com.tinqinacademy.comments.api.contracts.operations.editcomment.EditCommentInput;
import com.tinqinacademy.comments.api.contracts.operations.editcomment.EditCommentOutput;
import com.tinqinacademy.comments.core.operations.EditCommentOperationProcessor;
import com.tinqinacademy.comments.rest.CommentsApplication;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
@SpringBootTest(classes = CommentsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EditCommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EditCommentOperationProcessor editCommentOperationProcessor;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp () {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEditComment_Success () throws Exception {
        String commentId = "commentId123";
        EditCommentInput input = EditCommentInput.builder()
                .content("Updated comment content")
                .roomId("roomId123")
                .build();
        EditCommentOutput output = EditCommentOutput.builder()
                .commentId(commentId)
                .content("Updated comment content")
                .roomId("roomId123")
                .build();

        when(editCommentOperationProcessor.process(any(EditCommentInput.class)))
                .thenReturn(Either.right(output));

        mockMvc.perform(patch(RestApiRoutesComments.USER_EDIT, commentId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"commentId\":\"commentId123\",\"content\":\"Updated comment content\",\"roomId\":\"roomId123\"}"));
    }
}

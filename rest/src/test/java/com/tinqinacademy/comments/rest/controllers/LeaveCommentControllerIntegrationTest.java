package com.tinqinacademy.comments.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentInput;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentOutput;
import com.tinqinacademy.comments.core.operations.LeaveCommentOperationProcessor;
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

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
@SpringBootTest(classes = CommentsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LeaveCommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaveCommentOperationProcessor leaveCommentOperationProcessor;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLeaveComment_Success() throws Exception {
        LeaveCommentInput input = LeaveCommentInput.builder()
                .roomId("587ce90e-f191-4860-9d90-609652d43db2")
                .firstName("John")
                .lastName("Doe")
                .comment("Great room, enjoyed my stay!")
                .build();

        LeaveCommentOutput output = LeaveCommentOutput.builder()
                .roomId("587ce90e-f191-4860-9d90-609652d43db2")
                .comment("Great room, enjoyed my stay!")
                .publishedDate(LocalDateTime.now())
                .lastEditBy(LocalDateTime.now())
                .editedBy("John Doe")
                .build();

        when(leaveCommentOperationProcessor.process(any(LeaveCommentInput.class)))
                .thenReturn(Either.right(output));

        mockMvc.perform(post("/api/v1/hotel/587ce90e-f191-4860-9d90-609652d43db2/comment")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"roomId\":\"587ce90e-f191-4860-9d90-609652d43db2\",\"comment\":\"Great room, enjoyed my stay!\"}"));
    }
}
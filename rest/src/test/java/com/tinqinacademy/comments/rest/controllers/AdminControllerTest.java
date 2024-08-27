package com.tinqinacademy.comments.rest.controllers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.comments.api.contracts.routes.RestApiRoutesComments;
import com.tinqinacademy.comments.api.contracts.operations.admindelete.AdminDeleteOutput;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditInput;
import com.tinqinacademy.comments.api.errors.Errors;
import com.tinqinacademy.comments.persistence.entities.CommentEntity;
import com.tinqinacademy.comments.persistence.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {CommentTestConfig.class})
@ComponentScan(basePackages = {"com.tinqinacademy.comments"})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CommentEntity testComment;

    private final UUID TEST_ROOM_ID = UUID.fromString("eb4928c6-3c01-478c-b0b7-675a87bd8a91");

    @BeforeEach
    void setUp () {
        testComment = CommentEntity.builder()
                .id(UUID.randomUUID())
                .roomId(TEST_ROOM_ID)
                .firstName("John")
                .lastName("Doe")
                .comment("This is a test comment.")
                .publishedDate(LocalDateTime.now())
                .build();

        commentRepository.save(testComment);
    }

    @Test
    void testEditComment_Success () throws Exception {
        AdminEditInput adminEditInput = AdminEditInput.builder()
                .content("This is edited content.") // Using the 'content' field as per the original AdminEditInput class
                .build();

        MvcResult result = mockMvc.perform(patch(RestApiRoutesComments.ADMIN_EDIT, testComment.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(adminEditInput)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains("content"); // Checking for the 'content' field in the response

        CommentEntity updatedComment = commentRepository.findById(testComment.getId()).orElse(null);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getContent()).isEqualTo(adminEditInput.getContent()); // Verify that 'content' is updated
    }


    @Test
    void testEditComment_InvalidIdFormat() throws Exception {
        String invalidCommentId = "invalid-uuid-format";

        MvcResult result = mockMvc.perform(patch(RestApiRoutesComments.ADMIN_EDIT.replace("{commentId}", invalidCommentId))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new AdminEditInput())))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Errors response = objectMapper.readValue(responseBody, Errors.class);

        assertThat(response.getMessage()).contains("");
    }


    @Test
    void testDeleteComment_Success() throws Exception {
        // Ensure the comment is saved
        CommentEntity savedComment = commentRepository.save(testComment);
        String commentId = savedComment.getId().toString();  // Use the ID of the saved comment

        MvcResult result = mockMvc.perform(delete(RestApiRoutesComments.ADMIN_DELETE.replace("{commentId}", commentId)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        AdminDeleteOutput response = objectMapper.readValue(responseBody, AdminDeleteOutput.class);

        assertThat(response.getCommentId()).isEqualTo(commentId);
        assertThat(response.getMessage()).isEqualTo("Comment successfully deleted!");

        // Ensure the comment is deleted
        CommentEntity deletedComment = commentRepository.findById(savedComment.getId()).orElse(null);
        assertThat(deletedComment).isNull(); // Check that the comment has been deleted
    }



    @Test
    void testDeleteComment_NotFound() throws Exception {
        String nonExistentCommentId = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(delete(RestApiRoutesComments.ADMIN_DELETE.replace("{commentId}", nonExistentCommentId)))
                .andExpect(status().isNotFound())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        AdminDeleteOutput response = objectMapper.readValue(responseBody, AdminDeleteOutput.class);

        assertThat(response.getMessage()).isEqualTo("");
    }



}
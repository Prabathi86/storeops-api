package com.storeops.projects;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @Test
    void listProjects_ok() throws Exception {
        mockMvc.perform(get("/api/projects")
                .header("X-Store-Id", "store-1"))
                .andExpect(status().isOk());
    }

    @Test
    void createProject_ok() throws Exception {
        String body = """
            {
              "projectName": "Planogram Reset",
              "storeId": "store-1"
            }
            """;

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").exists());
    }

    @Test
    void addMember_ok() throws Exception {
        String body = """
            { "userId": "user-1", "role": "ASSOCIATE" }
            """;

        mockMvc.perform(post("/api/projects/project-1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }
}

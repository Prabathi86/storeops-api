package com.storeops.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired MockMvc mockMvc;

    @Test
    void listUsers_ok() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void getUser_ok() throws Exception {
        mockMvc.perform(get("/api/users/user-1"))
                .andExpect(status().isOk());
    }

    @Test
    void getUser_notFound() throws Exception {
        mockMvc.perform(get("/api/users/missing"))
                .andExpect(status().isNotFound());
    }
}

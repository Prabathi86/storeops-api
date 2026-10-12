package com.storeops.staff;

import com.storeops.StoreOpsApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = StoreOpsApplication.class)
@AutoConfigureMockMvc
class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listStaff_returnsOk() throws Exception {
        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk());
    }

    @Test
    void getStaffById_returnsOk() throws Exception {
        mockMvc.perform(get("/api/staff/staff-1"))
                .andExpect(status().isOk());
    }
}

package com.storeops.reports;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired MockMvc mockMvc;

    @Test
    void storeReports_ok() throws Exception {
        mockMvc.perform(get("/api/reports/stores/store-1"))
                .andExpect(status().isOk());
    }

    @Test
    void regionReports_ok() throws Exception {
        mockMvc.perform(get("/api/reports/regions/region-1"))
                .andExpect(status().isOk());
    }
}

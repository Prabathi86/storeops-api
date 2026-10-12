package com.storeops.reports;

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
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getStoreReports_returnsOk() throws Exception {
        mockMvc.perform(get("/api/reports/stores/store-1"))
                .andExpect(status().isOk());
    }

    @Test
    void getRegionReports_returnsOk() throws Exception {
        mockMvc.perform(get("/api/reports/regions/region-1"))
                .andExpect(status().isOk());
    }
}

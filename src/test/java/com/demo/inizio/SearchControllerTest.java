package com.demo.inizio;

import com.demo.inizio.controller.SearchController;
import com.demo.inizio.dto.SearchResult;
import com.demo.inizio.service.GoogleApiSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoogleApiSearchService searchService;

    @Test
    @DisplayName("/search returns results from service")
    void searchEndpointReturnsResults() throws Exception {
        List<SearchResult> results = List.of(
                new SearchResult("t1", "l1", "d1"),
                new SearchResult("t2", "l2", "d2")
        );
        when(searchService.search("test")).thenReturn(results);

        mockMvc.perform(get("/search").param("q", "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("t1"));
    }

    @Test
    @DisplayName("/download returns attachment with json")
    void downloadEndpointReturnsFile() throws Exception {
        List<SearchResult> results = List.of(new SearchResult("t1", "l1", "d1"));
        when(searchService.search("query")).thenReturn(results);

        mockMvc.perform(get("/download").param("q", "query"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"results.json\""))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("t1")));
    }
}
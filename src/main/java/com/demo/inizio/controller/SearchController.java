package com.demo.inizio.controller;

import com.demo.inizio.dto.SearchResult;
import com.demo.inizio.service.GoogleApiSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class SearchController {


    private final GoogleApiSearchService googleApiSearchService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SearchController(GoogleApiSearchService googleApiSearchService) {
        this.googleApiSearchService = googleApiSearchService;
    }



    //Endpoint  for search  only
    @GetMapping("/search")
    public List<SearchResult> getResults(@RequestParam String q) throws Exception {
        return googleApiSearchService.search(q);
    }

    //Endpoint for download jsdon file with results
    @GetMapping("/download")
    public ResponseEntity<String> downloadJson(@RequestParam String q) throws Exception {

        List<SearchResult> results = googleApiSearchService.search(q);

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(results);

        //return complete json file
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"results.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }

}

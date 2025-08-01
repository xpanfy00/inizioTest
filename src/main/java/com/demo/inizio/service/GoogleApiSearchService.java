package com.demo.inizio.service;


import com.demo.inizio.config.GoogleConfig;
import com.demo.inizio.dto.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleApiSearchService {
    private final GoogleConfig config;

    //URL for Google Custom Search API
    public static final String SEARCH_URL = "https://www.googleapis.com/customsearch/v1";

    public GoogleApiSearchService(GoogleConfig config) {
        this.config = config;
    }

    //Json parsing
    private final ObjectMapper mapper = new ObjectMapper();

    public List<SearchResult> search(String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String urlStr = String.format("%s?key=%s&cx=%s&q=%s", SEARCH_URL, config.getKey(), config.getCx(), encodedQuery);

        //HTTP GET request
        HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            throw new Exception(String.format("ERROR -> Got response code %d", connection.getResponseCode()));
        }

        try (InputStream is = connection.getInputStream()) {
            JsonNode root = mapper.readTree(is);
            List<SearchResult> results = new ArrayList<>();

            JsonNode items = root.get("items");

            if (items.isArray()){
                for (JsonNode item : items) {
                    String title = item.path("title").asText();
                    String link = item.path("link").asText();
                    String description = item.path("snippet").asText();

                    results.add(new SearchResult(title, link, description));
                }
            }
            return results;
        }
    }
}

package com.pengunie.health.search.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pengunie.health.search.service.SemanticSearchService;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SemanticSearchService searchService;

    public SearchController(
            SemanticSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public List<?> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "3") int limit) {

        return searchService.search(query, limit);
    }
}
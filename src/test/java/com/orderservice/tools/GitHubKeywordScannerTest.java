package com.orderservice.tools;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitHubKeywordScannerTest {

    @Test
    void parseKeywordsShouldHandleCommaSeparatedValues() {
        List<String> keywords = GitHubKeywordScanner.ScannerOptions.parseKeywords("RestController, FastAPI, spring");
        assertEquals(List.of("RestController", "FastAPI", "spring"), keywords);
    }

    @Test
    void findMatchedKeywordsShouldBeCaseInsensitive() {
        String content = "@RestController public class Demo {} # fastapi sample";
        Set<String> matched = GitHubKeywordScanner.findMatchedKeywords(content, List.of("RestController", "FastAPI"));

        assertTrue(matched.contains("RestController"));
        assertTrue(matched.contains("FastAPI"));
    }
}


package com.example.urlshortener.controller;

import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UrlController {
    @Autowired
    private UrlService urlService;

    @Operation(summary = "Shorten a long URL")
    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody Map<String, String> body) {
        String originalUrl = body.get("url");
        UrlMapping mapping = urlService.shortenUrl(originalUrl);
        return ResponseEntity.ok(Map.of(
            "shortUrl", mapping.getShortUrl(),
            "originalUrl", mapping.getOriginalUrl()
        ));
    }

    @Operation(summary = "Redirect to original URL")
    @GetMapping("/{shortUrl}")
    public RedirectView redirect(@PathVariable String shortUrl) {
        return urlService.getOriginalUrl(shortUrl)
            .map(mapping -> {
                urlService.incrementClickCount(mapping);
                return new RedirectView(mapping.getOriginalUrl());
            })
            .orElseGet(() -> new RedirectView("/not-found"));
    }

    @Operation(summary = "Get click count for a short URL")
    @GetMapping("/stats/{shortUrl}")
    public ResponseEntity<?> getStats(@PathVariable String shortUrl) {
        return urlService.getOriginalUrl(shortUrl)
            .map(mapping -> ResponseEntity.ok(Map.of(
                "shortUrl", mapping.getShortUrl(),
                "originalUrl", mapping.getOriginalUrl(),
                "clickCount", mapping.getClickCount()
            )))
            .orElse(ResponseEntity.notFound().build());
    }
}

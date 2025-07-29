package com.example.urlshortener.service;

import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import com.example.urlshortener.util.Base62Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UrlService {
    @Autowired
    private UrlMappingRepository repository;

    public UrlMapping shortenUrl(String originalUrl) {
        Optional<UrlMapping> existing = repository.findByOriginalUrl(originalUrl);
        if (existing.isPresent()) return existing.get();

        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(originalUrl);
        mapping = repository.save(mapping);

        String shortUrl = Base62Encoder.encode(mapping.getId());
        mapping.setShortUrl(shortUrl);
        return repository.save(mapping);
    }

    public Optional<UrlMapping> getOriginalUrl(String shortUrl) {
        return repository.findByShortUrl(shortUrl);
    }

    public void incrementClickCount(UrlMapping mapping) {
        mapping.setClickCount(mapping.getClickCount() + 1);
        repository.save(mapping);
    }
}

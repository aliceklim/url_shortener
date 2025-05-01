package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UrlDto;
import org.example.service.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {
    private final UrlService urlService;
    //VALIDATION

    @PostMapping
    public ResponseEntity<String> shortenUrl(@RequestBody UrlDto urlDto){
        return ResponseEntity.ok(urlService.generateShortUrl(urlDto));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<String> getOriginalUrl(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        return ResponseEntity.ok(originalUrl);
    }
}
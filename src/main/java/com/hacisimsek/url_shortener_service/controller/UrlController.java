package com.hacisimsek.url_shortener_service.controller;

import com.hacisimsek.url_shortener_service.model.Url;
import com.hacisimsek.url_shortener_service.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @PostMapping("/shorten")
    @ResponseBody
    public ResponseEntity<?> shortenUrl(@RequestParam String url,
                                        @RequestParam(required = false) Long userId) {
        try {
            Url shortened = urlService.createShortUrl(url, userId != null ? userId : 0L);
            return ResponseEntity.ok(shortened);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating short URL: " + e.getMessage());
        }
    }

    @GetMapping("/{shortCode}")
    public String redirect(@PathVariable String shortCode,
                           HttpServletRequest request) {
        Url url = urlService.getUrlByShortCode(shortCode);

        urlService.recordAnalytics(
                url,
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                request.getHeader("Referer"),
                null  // Location would need to be determined using a GeoIP service
        );

        return "redirect:" + url.getOriginalUrl();
    }
}
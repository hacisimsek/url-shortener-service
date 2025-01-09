package com.hacisimsek.url_shortener_service.service;

import com.hacisimsek.url_shortener_service.model.Url;
import com.hacisimsek.url_shortener_service.model.UrlAnalytics;
import com.hacisimsek.url_shortener_service.repository.UrlAnalyticsRepository;
import com.hacisimsek.url_shortener_service.repository.UrlRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlAnalyticsRepository analyticsRepository;
    private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    public UrlService(UrlRepository urlRepository, UrlAnalyticsRepository analyticsRepository) {
        this.urlRepository = urlRepository;
        this.analyticsRepository = analyticsRepository;
    }

    @Transactional
    public Url createShortUrl(String originalUrl, Long userId) {
        String shortCode;
        do {
            shortCode = generateShortCode();
        } while (urlRepository.findByShortCode(shortCode).isPresent());

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortCode(shortCode);
        url.setUserId(userId);

        return urlRepository.save(url);
    }

    @Cacheable(value = "urls", key = "#shortCode")
    public Url getUrlByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found"));
    }

    @Transactional
    public void recordAnalytics(Url url, String ipAddress, String userAgent, String referer, String location) {
        UrlAnalytics analytics = new UrlAnalytics();
        analytics.setUrlId(url.getId());
        analytics.setIpAddress(ipAddress);
        analytics.setUserAgent(userAgent);
        analytics.setReferer(referer);
        analytics.setLocation(location);

        analyticsRepository.save(analytics);
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
    }

    private String generateShortCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(ALLOWED_CHARS.charAt(random.nextInt(ALLOWED_CHARS.length())));
        }
        return code.toString();
    }
}
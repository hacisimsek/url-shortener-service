package com.hacisimsek.url_shortener_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "url_analytics")
public class UrlAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long urlId;

    @Column(nullable = false)
    private String ipAddress;

    @Column
    private String userAgent;

    @Column
    private String referer;

    @Column
    private String location;

    @Column(nullable = false)
    private LocalDateTime accessTime;

    @PrePersist
    protected void onCreate() {
        accessTime = LocalDateTime.now();
    }
}
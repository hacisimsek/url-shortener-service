package com.hacisimsek.url_shortener_service.repository;

import com.hacisimsek.url_shortener_service.model.UrlAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface UrlAnalyticsRepository extends JpaRepository<UrlAnalytics, Long> {
    List<UrlAnalytics> findByUrlId(Long urlId);

    @Query("SELECT a FROM UrlAnalytics a WHERE a.urlId = :urlId AND a.accessTime BETWEEN :startDate AND :endDate")
    List<UrlAnalytics> findAnalyticsByUrlIdAndDateRange(
            @Param("urlId") Long urlId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT a.referer, COUNT(a) FROM UrlAnalytics a WHERE a.urlId = :urlId GROUP BY a.referer")
    List<Object[]> findRefererStats(@Param("urlId") Long urlId);

    @Query("SELECT DATE(a.accessTime) as date, COUNT(a) as count FROM UrlAnalytics a " +
            "WHERE a.urlId = :urlId GROUP BY DATE(a.accessTime) ORDER BY date")
    List<Object[]> findDailyClickStats(@Param("urlId") Long urlId);

    @Query("SELECT a.location, COUNT(a) FROM UrlAnalytics a " +
            "WHERE a.urlId = :urlId GROUP BY a.location")
    List<Object[]> findLocationStats(@Param("urlId") Long urlId);
}
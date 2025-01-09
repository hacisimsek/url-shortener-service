package com.hacisimsek.url_shortener_service.repository;

import com.hacisimsek.url_shortener_service.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCode(String shortCode);

    List<Url> findByUserId(Long userId);

    @Query("SELECT u FROM Url u WHERE u.userId = :userId ORDER BY u.createdAt DESC")
    List<Url> findRecentUrlsByUser(@Param("userId") Long userId);

    @Query("SELECT u FROM Url u WHERE u.clickCount > :minClicks ORDER BY u.clickCount DESC")
    List<Url> findPopularUrls(@Param("minClicks") Long minClicks);

    boolean existsByShortCode(String shortCode);

    @Query("SELECT COUNT(u) FROM Url u WHERE u.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
}
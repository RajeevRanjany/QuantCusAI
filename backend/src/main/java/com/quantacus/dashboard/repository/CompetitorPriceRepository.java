package com.quantacus.dashboard.repository;

import com.quantacus.dashboard.entity.CompetitorPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CompetitorPriceRepository extends JpaRepository<CompetitorPrice, UUID> {

    List<CompetitorPrice> findByProductId(UUID productId);

    // Bulk delete — called before re-simulating prices on a refresh request.
    // Avoids loading all price rows into memory before deleting them.
    @Modifying
    @Query("DELETE FROM CompetitorPrice cp WHERE cp.product.id = :productId")
    void deleteAllByProductId(@Param("productId") UUID productId);
}

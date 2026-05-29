package com.quantacus.dashboard.repository;

import com.quantacus.dashboard.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Plain product list for a job — used internally by the pipeline
    List<Product> findByJobId(UUID jobId);

    // Loads products with their alerts and competitor prices for the dashboard.
    // @EntityGraph issues separate batch SELECTs for each collection instead of
    // a JOIN, which avoids the MultipleBagFetchException that occurs when two
    // @OneToMany collections are JOIN FETCHed in a single JPQL query.
    @EntityGraph(attributePaths = {"alerts", "competitorPrices"})
    List<Product> findWithDetailsByJobId(UUID jobId);

    // Loads a single product with its related collections
    @EntityGraph(attributePaths = {"alerts", "competitorPrices"})
    Optional<Product> findWithDetailsById(UUID id);

    long countByJobId(UUID jobId);

    // Duplicate detection: does another product (different id) share the same SKU?
    boolean existsBySkuIdAndIdNot(String skuId, UUID id);

    // Duplicate detection: does another product share the same title (case-insensitive)?
    boolean existsByProductTitleIgnoreCaseAndIdNot(String productTitle, UUID id);

    // Used for cross-job duplicate reporting — finds all products with a given SKU
    @Query("SELECT p FROM Product p WHERE p.skuId = :skuId AND p.id != :excludeId")
    List<Product> findDuplicatesBySku(@Param("skuId") String skuId, @Param("excludeId") UUID excludeId);
}

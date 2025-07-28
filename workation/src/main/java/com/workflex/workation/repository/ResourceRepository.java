package com.workflex.workation.repository;

import com.workflex.workation.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByTenantId(Long tenantId);

    Page<Resource> findByTenantId(Long tenantId, Pageable pageable);

    List<Resource> findByTenantIdAndOwnerId(Long tenantId, Long ownerId);

    Page<Resource> findByTenantIdAndOwnerId(Long tenantId, Long ownerId, Pageable pageable);

    Optional<Resource> findByIdAndTenantId(Long id, Long tenantId);

    Optional<Resource> findByIdAndTenantIdAndOwnerId(Long id, Long tenantId, Long ownerId);

    @Query("SELECT COUNT(r) FROM Resource r WHERE r.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT COUNT(r) FROM Resource r WHERE r.tenantId = :tenantId AND r.ownerId = :ownerId")
    long countByTenantIdAndOwnerId(@Param("tenantId") Long tenantId, @Param("ownerId") Long ownerId);

    // Search functionality
    @Query("SELECT r FROM Resource r WHERE r.tenantId = :tenantId AND r.name LIKE %:name%")
    Page<Resource> findByTenantIdAndNameContaining(@Param("tenantId") Long tenantId, @Param("name") String name, Pageable pageable);

    @Query("SELECT r FROM Resource r WHERE r.tenantId = :tenantId AND (r.name LIKE %:query% OR r.description LIKE %:query%)")
    Page<Resource> searchByTenantIdAndQuery(@Param("tenantId") Long tenantId, @Param("query") String query, Pageable pageable);

    @Query("SELECT r FROM Resource r WHERE r.tenantId = :tenantId AND r.ownerId = :ownerId AND (r.name LIKE %:query% OR r.description LIKE %:query%)")
    Page<Resource> searchByTenantIdAndOwnerIdAndQuery(@Param("tenantId") Long tenantId, @Param("ownerId") Long ownerId, @Param("query") String query, Pageable pageable);

    // Filter by resource type
    @Query("SELECT r FROM Resource r WHERE r.tenantId = :tenantId AND r.resourceType = :resourceType")
    Page<Resource> findByTenantIdAndResourceType(@Param("tenantId") Long tenantId, @Param("resourceType") String resourceType, Pageable pageable);

    // Filter by access level
    @Query("SELECT r FROM Resource r WHERE r.tenantId = :tenantId AND r.accessLevel = :accessLevel")
    Page<Resource> findByTenantIdAndAccessLevel(@Param("tenantId") Long tenantId, @Param("accessLevel") Resource.AccessLevel accessLevel, Pageable pageable);

    // Active resources only
    @Query("SELECT r FROM Resource r WHERE r.tenantId = :tenantId AND r.isActive = true")
    Page<Resource> findActiveByTenantId(@Param("tenantId") Long tenantId, Pageable pageable);

    // Complex search with multiple filters
    @Query("SELECT r FROM Resource r WHERE r.tenantId = :tenantId " +
           "AND (:name IS NULL OR r.name LIKE %:name%) " +
           "AND (:ownerId IS NULL OR r.ownerId = :ownerId) " +
           "AND (:resourceType IS NULL OR r.resourceType = :resourceType) " +
           "AND (:accessLevel IS NULL OR r.accessLevel = :accessLevel) " +
           "AND r.isActive = true")
    Page<Resource> findWithFilters(@Param("tenantId") Long tenantId,
                                   @Param("name") String name,
                                   @Param("ownerId") Long ownerId,
                                   @Param("resourceType") String resourceType,
                                   @Param("accessLevel") Resource.AccessLevel accessLevel,
                                   Pageable pageable);

    // Get distinct resource types for a tenant
    @Query("SELECT DISTINCT r.resourceType FROM Resource r WHERE r.tenantId = :tenantId AND r.resourceType IS NOT NULL")
    List<String> findDistinctResourceTypesByTenantId(@Param("tenantId") Long tenantId);

    // Check if user can access resource (for shared resources)
    @Query("SELECT r FROM Resource r WHERE r.tenantId = :tenantId AND r.id = :resourceId " +
           "AND (r.ownerId = :userId OR r.accessLevel IN ('SHARED', 'PUBLIC'))")
    Optional<Resource> findAccessibleResource(@Param("tenantId") Long tenantId, @Param("resourceId") Long resourceId, @Param("userId") Long userId);

    boolean existsByIdAndTenantId(Long id, Long tenantId);

    boolean existsByIdAndTenantIdAndOwnerId(Long id, Long tenantId, Long ownerId);
}
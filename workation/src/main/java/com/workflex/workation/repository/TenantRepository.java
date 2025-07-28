package com.workflex.workation.repository;

import com.workflex.workation.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByName(String name);

    Optional<Tenant> findBySchemaName(String schemaName);

    List<Tenant> findByIsActive(Boolean isActive);

    @Query("SELECT t FROM Tenant t WHERE t.isActive = true")
    List<Tenant> findAllActiveTenants();

    @Query("SELECT t FROM Tenant t WHERE t.isActive = true AND t.name LIKE %:name%")
    List<Tenant> findActiveTenantsByNameContaining(@Param("name") String name);

    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.isActive = true")
    long countActiveTenants();

    boolean existsByName(String name);

    boolean existsBySchemaName(String schemaName);

    @Query("SELECT t.schemaName FROM Tenant t WHERE t.id = :tenantId")
    Optional<String> findSchemaNameById(@Param("tenantId") Long tenantId);
}
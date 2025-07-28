package com.workflex.workation.service;

import com.workflex.workation.dto.TenantDto;
import com.workflex.workation.model.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TenantService {

    TenantDto createTenant(TenantDto tenantDto);

    TenantDto getTenantById(Long id);

    TenantDto getTenantByName(String name);

    List<TenantDto> getAllActiveTenants();

    Page<TenantDto> getAllTenants(Pageable pageable);

    TenantDto updateTenant(Long id, TenantDto tenantDto);

    void deleteTenant(Long id);

    void deactivateTenant(Long id);

    void activateTenant(Long id);

    Tenant findEntityById(Long id);

    Tenant findEntityBySchemaName(String schemaName);

    boolean tenantExistsById(Long id);

    boolean tenantExistsByName(String name);

    long getTenantUserCount(Long tenantId);

    long getTenantResourceCount(Long tenantId);
}
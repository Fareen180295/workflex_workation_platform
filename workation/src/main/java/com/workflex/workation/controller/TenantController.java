package com.workflex.workation.controller;

import com.workflex.workation.dto.TenantDto;
import com.workflex.workation.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
@Slf4j
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<TenantDto> createTenant(@Valid @RequestBody TenantDto tenantDto) {
        log.info("Creating tenant: {}", tenantDto.getName());
        
        TenantDto createdTenant = tenantService.createTenant(tenantDto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTenant);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<TenantDto> getTenant(@PathVariable Long id) {
        TenantDto tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(tenant);
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<TenantDto>> getAllTenants(Pageable pageable) {
        Page<TenantDto> tenants = tenantService.getAllTenants(pageable);
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<TenantDto>> getActiveTenants() {
        List<TenantDto> tenants = tenantService.getAllActiveTenants();
        return ResponseEntity.ok(tenants);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<TenantDto> updateTenant(@PathVariable Long id, 
                                                  @Valid @RequestBody TenantDto tenantDto) {
        log.info("Updating tenant: {}", id);
        
        TenantDto updatedTenant = tenantService.updateTenant(id, tenantDto);
        
        return ResponseEntity.ok(updatedTenant);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        log.info("Deleting tenant: {}", id);
        
        tenantService.deleteTenant(id);
        
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deactivateTenant(@PathVariable Long id) {
        log.info("Deactivating tenant: {}", id);
        
        tenantService.deactivateTenant(id);
        
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> activateTenant(@PathVariable Long id) {
        log.info("Activating tenant: {}", id);
        
        tenantService.activateTenant(id);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/stats")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Object> getTenantStats(@PathVariable Long id) {
        TenantDto tenant = tenantService.getTenantById(id);
        long userCount = tenantService.getTenantUserCount(id);
        long resourceCount = tenantService.getTenantResourceCount(id);
        
        return ResponseEntity.ok(new Object() {
            public final TenantDto tenant_info = tenant;
            public final long user_count = userCount;
            public final long resource_count = resourceCount;
            public final long max_users = tenant.getMaxUsers();
            public final long max_resources = tenant.getMaxResources();
        });
    }
}
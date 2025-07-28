package com.workflex.workation.service.impl;

import com.workflex.workation.dto.TenantDto;
import com.workflex.workation.exception.ResourceNotFoundException;
import com.workflex.workation.exception.TenantException;
import com.workflex.workation.model.Tenant;
import com.workflex.workation.repository.ResourceRepository;
import com.workflex.workation.repository.TenantRepository;
import com.workflex.workation.repository.UserRepository;
import com.workflex.workation.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final DataSource dataSource;

    @Override
    public TenantDto createTenant(TenantDto tenantDto) {
        log.info("Creating new tenant: {}", tenantDto.getName());
        
        if (tenantRepository.findByName(tenantDto.getName()).isPresent()) {
            throw new TenantException("Tenant with name '" + tenantDto.getName() + "' already exists");
        }
        
        if (tenantRepository.findBySchemaName(tenantDto.getSchemaName()).isPresent()) {
            throw new TenantException("Tenant with schema name '" + tenantDto.getSchemaName() + "' already exists");
        }

        Tenant tenant = new Tenant();
        tenant.setName(tenantDto.getName());
        tenant.setSchemaName(tenantDto.getSchemaName());
        tenant.setMaxUsers(tenantDto.getMaxUsers() != null ? tenantDto.getMaxUsers() : 50);
        tenant.setMaxResources(tenantDto.getMaxResources() != null ? tenantDto.getMaxResources() : 500);
        tenant.setContactEmail(tenantDto.getContactEmail());
        tenant.setIsActive(true);

        // Create database schema for the tenant
        createTenantSchema(tenant.getSchemaName());
        
        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("Tenant created successfully with ID: {}", savedTenant.getId());
        
        return convertToDto(savedTenant);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantDto getTenantById(Long id) {
        Tenant tenant = findEntityById(id);
        return convertToDto(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantDto getTenantByName(String name) {
        Tenant tenant = tenantRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with name: " + name));
        return convertToDto(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantDto> getAllActiveTenants() {
        return tenantRepository.findByIsActive(true)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TenantDto> getAllTenants(Pageable pageable) {
        return tenantRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public TenantDto updateTenant(Long id, TenantDto tenantDto) {
        log.info("Updating tenant with ID: {}", id);
        
        Tenant tenant = findEntityById(id);
        
        if (!tenant.getName().equals(tenantDto.getName()) && 
            tenantRepository.findByName(tenantDto.getName()).isPresent()) {
            throw new TenantException("Tenant with name '" + tenantDto.getName() + "' already exists");
        }

        tenant.setName(tenantDto.getName());
        tenant.setMaxUsers(tenantDto.getMaxUsers() != null ? tenantDto.getMaxUsers() : tenant.getMaxUsers());
        tenant.setMaxResources(tenantDto.getMaxResources() != null ? tenantDto.getMaxResources() : tenant.getMaxResources());
        tenant.setContactEmail(tenantDto.getContactEmail());

        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("Tenant updated successfully: {}", savedTenant.getId());
        
        return convertToDto(savedTenant);
    }

    @Override
    public void deleteTenant(Long id) {
        log.info("Deleting tenant with ID: {}", id);
        
        Tenant tenant = findEntityById(id);
        
        // Drop the tenant schema
        dropTenantSchema(tenant.getSchemaName());
        
        tenantRepository.delete(tenant);
        log.info("Tenant deleted successfully: {}", id);
    }

    @Override
    public void deactivateTenant(Long id) {
        log.info("Deactivating tenant with ID: {}", id);
        
        Tenant tenant = findEntityById(id);
        tenant.setIsActive(false);
        tenantRepository.save(tenant);
        
        log.info("Tenant deactivated successfully: {}", id);
    }

    @Override
    public void activateTenant(Long id) {
        log.info("Activating tenant with ID: {}", id);
        
        Tenant tenant = findEntityById(id);
        tenant.setIsActive(true);
        tenantRepository.save(tenant);
        
        log.info("Tenant activated successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Tenant findEntityById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Tenant findEntityBySchemaName(String schemaName) {
        return tenantRepository.findBySchemaName(schemaName)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with schema name: " + schemaName));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tenantExistsById(Long id) {
        return tenantRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tenantExistsByName(String name) {
        return tenantRepository.findByName(name).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTenantUserCount(Long tenantId) {
        return userRepository.countByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTenantResourceCount(Long tenantId) {
        return resourceRepository.countByTenantId(tenantId);
    }

    private void createTenantSchema(String schemaName) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            
            String sql = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
            statement.execute(sql);
            log.info("Schema created successfully: {}", schemaName);
            
        } catch (SQLException e) {
            log.error("Failed to create schema: {}", schemaName, e);
            throw new TenantException("Failed to create database schema for tenant", e);
        }
    }

    private void dropTenantSchema(String schemaName) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            
            String sql = "DROP SCHEMA IF EXISTS " + schemaName + " CASCADE";
            statement.execute(sql);
            log.info("Schema dropped successfully: {}", schemaName);
            
        } catch (SQLException e) {
            log.error("Failed to drop schema: {}", schemaName, e);
            throw new TenantException("Failed to drop database schema for tenant", e);
        }
    }

    private TenantDto convertToDto(Tenant tenant) {
        TenantDto dto = new TenantDto();
        dto.setId(tenant.getId());
        dto.setName(tenant.getName());
        dto.setSchemaName(tenant.getSchemaName());
        dto.setMaxUsers(tenant.getMaxUsers());
        dto.setMaxResources(tenant.getMaxResources());
        dto.setContactEmail(tenant.getContactEmail());
        dto.setIsActive(tenant.getIsActive());
        dto.setCreatedAt(tenant.getCreatedAt());
        dto.setUpdatedAt(tenant.getUpdatedAt());
        return dto;
    }
}
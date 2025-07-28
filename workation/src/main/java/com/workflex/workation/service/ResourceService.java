package com.workflex.workation.service;

import com.workflex.workation.dto.ResourceDto;
import com.workflex.workation.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResourceService {

    ResourceDto createResource(ResourceDto resourceDto);

    ResourceDto getResourceById(Long id);

    List<ResourceDto> getResourcesByTenant(Long tenantId);

    Page<ResourceDto> getResourcesByTenant(Long tenantId, Pageable pageable);

    List<ResourceDto> getResourcesByOwner(Long ownerId, Long tenantId);

    Page<ResourceDto> searchResources(String name, Long ownerId, Long tenantId, Pageable pageable);

    ResourceDto updateResource(Long id, ResourceDto resourceDto);

    void deleteResource(Long id);

    Resource findEntityById(Long id);

    boolean resourceExistsById(Long id);

    boolean canUserAccessResource(Long userId, Long resourceId);

    boolean canUserModifyResource(Long userId, Long resourceId);

    List<ResourceDto> getResourcesByOwnerCount(Long ownerId, Long tenantId);
}
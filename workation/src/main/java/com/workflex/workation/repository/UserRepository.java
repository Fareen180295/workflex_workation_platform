package com.workflex.workation.repository;

import com.workflex.workation.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndTenantId(String username, Long tenantId);

    List<User> findByTenantId(Long tenantId);

    Page<User> findByTenantId(Long tenantId, Pageable pageable);

    List<User> findByTenantIdAndRole(Long tenantId, User.UserRole role);

    List<User> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);

    @Query("SELECT COUNT(u) FROM User u WHERE u.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.tenantId = :tenantId AND u.isActive = true")
    long countActiveByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.username LIKE %:username%")
    List<User> findByTenantIdAndUsernameContaining(@Param("tenantId") Long tenantId, @Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.role = :role AND u.isActive = true")
    List<User> findActiveByTenantIdAndRole(@Param("tenantId") Long tenantId, @Param("role") User.UserRole role);

    boolean existsByUsernameAndTenantId(String username, Long tenantId);

    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.lastLogin < :date")
    List<User> findInactiveUsers(@Param("tenantId") Long tenantId, @Param("date") LocalDateTime date);

    @Query("SELECT DISTINCT u.role FROM User u WHERE u.tenantId = :tenantId")
    List<User.UserRole> findDistinctRolesByTenantId(@Param("tenantId") Long tenantId);

    // For resource ownership validation
    @Query("SELECT u.tenantId FROM User u WHERE u.id = :userId")
    Optional<Long> findTenantIdByUserId(@Param("userId") Long userId);
}
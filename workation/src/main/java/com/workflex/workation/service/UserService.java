package com.workflex.workation.service;

import com.workflex.workation.dto.UserDto;
import com.workflex.workation.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long id);

    UserDto getUserByUsername(String username, Long tenantId);

    List<UserDto> getUsersByTenant(Long tenantId);

    Page<UserDto> getUsersByTenant(Long tenantId, Pageable pageable);

    List<UserDto> getUsersByRole(User.UserRole role, Long tenantId);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    void deactivateUser(Long id);

    void activateUser(Long id);

    User findEntityById(Long id);

    User findEntityByUsernameAndTenant(String username, Long tenantId);

    boolean userExistsById(Long id);

    boolean userExistsByUsername(String username, Long tenantId);

    long getUserResourceCount(Long userId);

    boolean canUserCreateResource(Long userId);
}
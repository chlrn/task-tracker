package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Controller", description = "API для управления пользователями и ролями")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Operation(summary = "Получить всех пользователей")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @Operation(summary = "Добавить новую роль")
    @PostMapping("/roles")
    public ResponseEntity<Role> addRole(@RequestBody Role role) {
        Role savedRole = roleRepository.save(role);
        return ResponseEntity.ok(savedRole);
    }

    @Operation(summary = "Назначить роль пользователю")
    @PostMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<User> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Роль не найдена"));

        user.getRoles().add(role);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Удалить роль у пользователя")
    @DeleteMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<User> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Роль не найдена"));

        user.getRoles().remove(role);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}

package com.NTG.Cridir.controller;

import com.NTG.Cridir.DTOs.UserProfileDTO;
import com.NTG.Cridir.DTOs.UserUpdateRequest;
import com.NTG.Cridir.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 4. GET /api/users/me
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('PROVIDER')")
    @GetMapping("/me")
    public UserProfileDTO getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getProfile(userDetails.getUsername());
    }

    // 5. PUT /api/users/me
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('PROVIDER')")
    @PutMapping("/me")
    public UserProfileDTO updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserUpdateRequest request
    ) {
        return userService.updateProfile(userDetails.getUsername(), request);
    }

    @PreAuthorize("hasRole('CUSTOMER') or hasRole('PROVIDER')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

}

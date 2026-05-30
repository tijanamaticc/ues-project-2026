package rs.projectues.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rs.projectues.dto.ChangePasswordRequest;
import rs.projectues.dto.ProfileUpdateRequest;
import rs.projectues.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {
    private final AuthService authService;

    public ProfileController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public Map<String, Object> profile(Authentication authentication) {
        return authService.getProfile(authentication.getName());
    }

    @PutMapping
    public Map<String, Object> update(Authentication authentication, @RequestBody ProfileUpdateRequest request) {
        return authService.updateProfile(authentication.getName(), request);
    }

    @PostMapping("/password")
    public ResponseEntity<Map<String, Object>> changePassword(Authentication authentication, @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(authentication.getName(), request));
    }
}
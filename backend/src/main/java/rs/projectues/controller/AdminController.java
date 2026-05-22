package rs.projectues.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.projectues.entity.RegistrationRequest;
import rs.projectues.entity.RequestStatus;
import rs.projectues.repository.RegistrationRequestRepository;
import rs.projectues.service.AuthService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    private final RegistrationRequestRepository requestRepository;
    private final AuthService authService;

    public AdminController(RegistrationRequestRepository requestRepository, AuthService authService) {
        this.requestRepository = requestRepository;
        this.authService = authService;
    }

    @GetMapping("/requests")
    public List<RegistrationRequest> pendingRequests() {
        return requestRepository.findByStatus(RequestStatus.PENDING);
    }

    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        return ResponseEntity.ok(authService.approveRequest(id));
    }
}

package rs.projectues.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.projectues.dto.LoginRequest;
import rs.projectues.dto.RegisterRequest;
import rs.projectues.entity.RegistrationRequest;
import rs.projectues.entity.RequestStatus;
import rs.projectues.entity.Role;
import rs.projectues.entity.User;
import rs.projectues.repository.RegistrationRequestRepository;
import rs.projectues.repository.UserRepository;
import rs.projectues.security.JwtService;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RegistrationRequestRepository requestRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, RegistrationRequestRepository requestRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegistrationRequest register(RegisterRequest request) {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail(request.getEmail());
        registrationRequest.setPassword(passwordEncoder.encode(request.getPassword()));
        registrationRequest.setName(request.getName());
        registrationRequest.setAddress(request.getAddress());
        registrationRequest.setCity(request.getCity());
        registrationRequest.setPhoneNumber(request.getPhoneNumber());
        registrationRequest.setStatus(RequestStatus.PENDING);
        return requestRepository.save(registrationRequest);
    }

    public Map<String, Object> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Korisnik ne postoji ili nije odobren"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Pogrešna lozinka");
        }
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("email", user.getEmail());
        response.put("name", user.getName());
        return response;
    }

    public User approveRequest(Long requestId) {
        RegistrationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Zahtev ne postoji"));
        request.setStatus(RequestStatus.APPROVED);
        requestRepository.save(request);

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(Role.USER);
        return userRepository.save(user);
    }
}

package rs.projectues.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.projectues.dto.ChangePasswordRequest;
import rs.projectues.dto.ProfileUpdateRequest;
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
        String username = normalizeUsername(request.getUsername(), request.getEmail());
        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Korisnik sa tim emailom ili korisničkim imenom već postoji");
        }

        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail(request.getEmail());
        registrationRequest.setUsername(username);
        registrationRequest.setPassword(passwordEncoder.encode(request.getPassword()));
        registrationRequest.setFirstName(request.getFirstName());
        registrationRequest.setLastName(request.getLastName());
        registrationRequest.setAddress(request.getAddress());
        registrationRequest.setCity(request.getCity());
        registrationRequest.setPhoneNumber(request.getPhoneNumber());
        registrationRequest.setStatus(RequestStatus.PENDING);
        return requestRepository.save(registrationRequest);
    }

    public Map<String, Object> login(LoginRequest request) {
        User user = userRepository.findByEmailOrUsername(request.getEmail(), request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Korisnik ne postoji ili nije odobren"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Pogrešna lozinka");
        }
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("email", user.getEmail());
        response.put("username", user.getUsername());
        response.put("name", (user.getFirstName() == null ? "" : user.getFirstName()) + " " + (user.getLastName() == null ? "" : user.getLastName()));
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("address", user.getAddress());
        response.put("city", user.getCity());
        response.put("phoneNumber", user.getPhoneNumber());
        return response;
    }

    public User approveRequest(Long requestId) {
        RegistrationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Zahtev ne postoji"));
        // prevent creating duplicate users
        String username = normalizeUsername(request.getUsername(), request.getEmail());
        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Korisnik sa tim emailom već postoji");
        }

        // Ensure password is encoded before saving to User. The stored request password
        // may be either raw or already encoded; detect bcrypt prefix to decide.
        String pwd = request.getPassword();
        boolean looksEncoded = pwd != null && pwd.startsWith("$2");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(username);
        user.setPassword(looksEncoded ? pwd : passwordEncoder.encode(pwd));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(Role.USER);
        User saved = userRepository.save(user);

        request.setStatus(RequestStatus.APPROVED);
        requestRepository.save(request);

        return saved;
    }

    public Map<String, Object> getProfile(String email) {
        return toProfileMap(loadUser(email));
    }

    public Map<String, Object> updateProfile(String email, ProfileUpdateRequest request) {
        User user = loadUser(email);
        String desiredEmail = normalizeEmail(request.getEmail(), user.getEmail());
        String desiredUsername = normalizeUsername(request.getUsername(), user.getUsername());

        if (!desiredEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(desiredEmail)) {
            throw new IllegalArgumentException("Korisnik sa tim emailom već postoji");
        }
        if (!desiredUsername.equalsIgnoreCase(user.getUsername()) && userRepository.existsByUsername(desiredUsername)) {
            throw new IllegalArgumentException("Korisnik sa tim korisničkim imenom već postoji");
        }

        user.setEmail(desiredEmail);
        user.setUsername(desiredUsername);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setPhoneNumber(request.getPhoneNumber());
        userRepository.save(user);
        return toProfileMap(user);
    }

    public Map<String, Object> changePassword(String email, ChangePasswordRequest request) {
        User user = loadUser(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Trenutna lozinka nije ispravna");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("Nova lozinka je obavezna");
        }
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Nove lozinke se ne poklapaju");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return Map.of("message", "Lozinka je uspešno promenjena");
    }

    private User loadUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Korisnik ne postoji"));
    }

    private Map<String, Object> toProfileMap(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("username", user.getUsername());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("address", user.getAddress());
        response.put("city", user.getCity());
        response.put("phoneNumber", user.getPhoneNumber());
        response.put("name", (user.getFirstName() == null ? "" : user.getFirstName()) + " " + (user.getLastName() == null ? "" : user.getLastName()));
        response.put("role", user.getRole());
        return response;
    }

    private String normalizeUsername(String username, String fallbackEmail) {
        if (username != null && !username.isBlank()) {
            return username.trim();
        }
        return normalizeEmail(fallbackEmail, fallbackEmail).split("@")[0];
    }

    private String normalizeEmail(String email, String fallback) {
        String value = email == null || email.isBlank() ? fallback : email;
        return value == null ? "" : value.trim().toLowerCase();
    }
}

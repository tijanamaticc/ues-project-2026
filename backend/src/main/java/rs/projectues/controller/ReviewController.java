package rs.projectues.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rs.projectues.dto.ReviewRequest;
import rs.projectues.entity.Review;
import rs.projectues.entity.User;
import rs.projectues.repository.EventRepository;
import rs.projectues.repository.ReviewRepository;
import rs.projectues.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public ReviewController(ReviewRepository reviewRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/place/{placeId}")
    public List<Review> forPlace(@PathVariable Long placeId) {
        return reviewRepository.findByPlaceIdOrderByCreatedAtDesc(placeId);
    }

    @PostMapping("/place/{placeId}")
    public ResponseEntity<Review> create(@PathVariable Long placeId, @RequestBody ReviewRequest request, Authentication authentication) {
        User user = authentication != null ? userRepository.findByEmail(authentication.getName()).orElse(null) : null;
        if (user != null && user.getRole() == rs.projectues.entity.Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        Review review = new Review();
        review.setPlaceId(placeId);
        review.setEventId(request.getEventId());
        review.setUserEmail(authentication != null ? authentication.getName() : request.getUserEmail());
        review.setReviewerUsername(user != null ? user.getUsername() : null);
        review.setReviewerName(user != null ? buildReviewerName(user) : null);
        if (request.getEventId() != null) {
            eventRepository.findById(request.getEventId()).ifPresent(event -> review.setEventName(event.getName()));
        }
        review.setRating(request.getOverallRating() != null ? request.getOverallRating() : request.getRating());
        review.setOverallRating(request.getOverallRating() != null ? request.getOverallRating() : request.getRating());
        review.setPerformanceRating(request.getPerformanceRating());
        review.setSoundLightRating(request.getSoundLightRating());
        review.setSpaceRating(request.getSpaceRating());
        review.setText(request.getText());
        Review saved = reviewRepository.save(review);
        return ResponseEntity.ok(saved);
    }

    private String buildReviewerName(User user) {
        String firstName = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isBlank() ? user.getUsername() : fullName;
    }
}

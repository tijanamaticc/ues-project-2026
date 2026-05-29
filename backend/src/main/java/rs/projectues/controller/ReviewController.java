package rs.projectues.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.projectues.entity.Review;
import rs.projectues.repository.ReviewRepository;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/place/{placeId}")
    public List<Review> forPlace(@PathVariable Long placeId) {
        return reviewRepository.findByPlaceIdOrderByCreatedAtDesc(placeId);
    }

    @PostMapping("/place/{placeId}")
    public ResponseEntity<Review> create(@PathVariable Long placeId, @RequestBody Review review) {
        review.setPlaceId(placeId);
        Review saved = reviewRepository.save(review);
        return ResponseEntity.ok(saved);
    }
}

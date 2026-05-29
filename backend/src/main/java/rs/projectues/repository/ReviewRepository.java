package rs.projectues.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.projectues.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPlaceIdOrderByCreatedAtDesc(Long placeId);
}

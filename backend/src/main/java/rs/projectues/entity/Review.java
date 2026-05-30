package rs.projectues.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long placeId;
    private Long eventId;
    private String userEmail;
    private String reviewerUsername;
    private String reviewerName;
    private String eventName;
    private Integer rating;
    private Integer performanceRating;
    private Integer soundLightRating;
    private Integer spaceRating;
    private Integer overallRating;

    @Column(length = 4000)
    private String text;

    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlaceId() { return placeId; }
    public void setPlaceId(Long placeId) { this.placeId = placeId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getReviewerUsername() { return reviewerUsername; }
    public void setReviewerUsername(String reviewerUsername) { this.reviewerUsername = reviewerUsername; }
    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public Integer getPerformanceRating() { return performanceRating; }
    public void setPerformanceRating(Integer performanceRating) { this.performanceRating = performanceRating; }
    public Integer getSoundLightRating() { return soundLightRating; }
    public void setSoundLightRating(Integer soundLightRating) { this.soundLightRating = soundLightRating; }
    public Integer getSpaceRating() { return spaceRating; }
    public void setSpaceRating(Integer spaceRating) { this.spaceRating = spaceRating; }
    public Integer getOverallRating() { return overallRating; }
    public void setOverallRating(Integer overallRating) { this.overallRating = overallRating; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

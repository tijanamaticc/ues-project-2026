package rs.projectues.dto;

public class ReviewRequest {
    private Long eventId;
    private String userEmail;
    private Integer rating;
    private Integer performanceRating;
    private Integer soundLightRating;
    private Integer spaceRating;
    private Integer overallRating;
    private String text;

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
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
}
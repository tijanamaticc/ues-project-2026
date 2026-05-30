package rs.projectues.dto;

import java.time.LocalDate;

public class EventRequest {
    private String name;
    private Long placeId;
    private String placeName;
    private String address;
    private String type;
    private LocalDate eventDate;
    private Boolean recurring;
    private Double entryPrice;
    private Boolean freeEntry;
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getPlaceId() { return placeId; }
    public void setPlaceId(Long placeId) { this.placeId = placeId; }
    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public Boolean getRecurring() { return recurring; }
    public void setRecurring(Boolean recurring) { this.recurring = recurring; }
    public Double getEntryPrice() { return entryPrice; }
    public void setEntryPrice(Double entryPrice) { this.entryPrice = entryPrice; }
    public Boolean getFreeEntry() { return freeEntry; }
    public void setFreeEntry(Boolean freeEntry) { this.freeEntry = freeEntry; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
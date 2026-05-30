package rs.projectues.service;

import org.springframework.stereotype.Service;
import rs.projectues.dto.EventRequest;
import rs.projectues.entity.Event;
import rs.projectues.entity.Place;
import rs.projectues.repository.PlaceRepository;
import rs.projectues.repository.EventRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;

    public EventService(EventRepository eventRepository, PlaceRepository placeRepository) {
        this.eventRepository = eventRepository;
        this.placeRepository = placeRepository;
    }

    public List<Event> findAll(String query, String type, String place, String address, Double maxPrice, Boolean freeOnly, Boolean todayOnly) {
        return eventRepository.findAll().stream()
                .filter(event -> matchesText(event, query))
                .filter(event -> matchesText(event.getType(), type))
                .filter(event -> matchesText(event.getPlaceName(), place))
                .filter(event -> matchesText(event.getAddress(), address))
                .filter(event -> maxPrice == null || event.getFreeEntry() == Boolean.TRUE || (event.getEntryPrice() != null && event.getEntryPrice() <= maxPrice))
                .filter(event -> freeOnly == null || !freeOnly || event.getFreeEntry() == Boolean.TRUE)
                .filter(event -> todayOnly == null || !todayOnly || Objects.equals(event.getEventDate(), LocalDate.now()))
                .collect(Collectors.toList());
    }

    public List<Event> findByPlace(Long placeId) {
        List<Event> directMatches = eventRepository.findByPlaceId(placeId);
        Place place = placeRepository.findById(placeId).orElse(null);
        if (place == null || place.getName() == null || place.getName().isBlank()) {
            return directMatches;
        }

        String placeName = place.getName().trim().toLowerCase(Locale.ROOT);
        return eventRepository.findAll().stream()
            .filter(event -> (event.getPlaceId() != null && event.getPlaceId().equals(placeId))
                || (event.getPlaceName() != null && event.getPlaceName().toLowerCase(Locale.ROOT).contains(placeName)))
            .collect(Collectors.toList());
    }

    public Event create(EventRequest request) {
        Event event = new Event();
        apply(event, request);
        // ensure event is linked to an existing place; try to resolve by placeName when placeId missing
        if (event.getPlaceId() == null) {
            if (event.getPlaceName() != null && !event.getPlaceName().isBlank()) {
                Place found = placeRepository.findByName(event.getPlaceName()).orElse(null);
                if (found != null) event.setPlaceId(found.getId());
            }
            if (event.getPlaceId() == null) {
                throw new IllegalArgumentException("Događaj mora biti vezan za postojeće mesto (placeId)");
            }
        }
        return eventRepository.save(event);
    }

    public Event update(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Događaj ne postoji"));
        apply(event, request);
        if (event.getPlaceId() == null) {
            if (event.getPlaceName() != null && !event.getPlaceName().isBlank()) {
                Place found = placeRepository.findByName(event.getPlaceName()).orElse(null);
                if (found != null) event.setPlaceId(found.getId());
            }
            if (event.getPlaceId() == null) {
                throw new IllegalArgumentException("Događaj mora biti vezan za postojeće mesto (placeId)");
            }
        }
        return eventRepository.save(event);
    }

    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    private void apply(Event event, EventRequest request) {
        event.setName(request.getName());
        event.setPlaceId(request.getPlaceId());
        event.setPlaceName(request.getPlaceName());
        event.setAddress(request.getAddress());
        event.setType(request.getType());
        event.setEventDate(request.getEventDate());
        event.setRecurring(Boolean.TRUE.equals(request.getRecurring()));
        event.setFreeEntry(Boolean.TRUE.equals(request.getFreeEntry()));
        event.setEntryPrice(Boolean.TRUE.equals(request.getFreeEntry()) ? 0.0 : request.getEntryPrice());
        event.setDescription(request.getDescription());
    }

    private boolean matchesText(Event event, String value) {
        return matchesText(event.getName(), value) || matchesText(event.getPlaceName(), value) || matchesText(event.getAddress(), value) || matchesText(event.getType(), value);
    }

    private boolean matchesText(String source, String value) {
        if (value == null || value.isBlank()) {
            return true;
        }
        if (source == null) {
            return false;
        }
        String src = source.toLowerCase(Locale.ROOT).trim();
        String val = value.toLowerCase(Locale.ROOT).trim();
        // use prefix (startsWith) matching so searching 'd' matches names starting with 'd'
        return src.startsWith(val);
    }
}
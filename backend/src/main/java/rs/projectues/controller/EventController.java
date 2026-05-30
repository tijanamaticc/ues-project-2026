package rs.projectues.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.projectues.dto.EventRequest;
import rs.projectues.entity.Event;
import rs.projectues.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> list(@RequestParam(required = false) String query,
                            @RequestParam(required = false) String type,
                            @RequestParam(required = false) String place,
                            @RequestParam(required = false) String address,
                            @RequestParam(required = false) Double maxPrice,
                            @RequestParam(required = false) Boolean freeOnly,
                            @RequestParam(required = false) Boolean todayOnly) {
        return eventService.findAll(query, type, place, address, maxPrice, freeOnly, todayOnly);
    }

    @GetMapping("/place/{placeId}")
    public List<Event> byPlace(@PathVariable Long placeId) {
        return eventService.findByPlace(placeId);
    }

    @PostMapping
    public Event create(@RequestBody EventRequest request) {
        try {
            return eventService.create(request);
        } catch (IllegalArgumentException ex) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Event update(@PathVariable Long id, @RequestBody EventRequest request) {
        try {
            return eventService.update(id, request);
        } catch (IllegalArgumentException ex) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
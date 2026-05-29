package rs.projectues.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.projectues.dto.PlaceRequest;
import rs.projectues.entity.Place;
import rs.projectues.service.PlaceService;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = "*")
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public List<Place> list(@RequestParam(required = false) String query) {
        return placeService.findAll(query);
    }

    @PostMapping
    public Place create(@RequestBody PlaceRequest request) {
        return placeService.create(request);
    }

    @PutMapping("/{id}")
    public Place update(@PathVariable Long id, @RequestBody PlaceRequest request) {
        return placeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        placeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

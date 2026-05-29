package rs.projectues.service;

import org.springframework.stereotype.Service;
import rs.projectues.dto.PlaceRequest;
import rs.projectues.entity.Place;
import rs.projectues.repository.PlaceRepository;

import java.util.List;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public List<Place> findAll(String query) {
        if (query == null || query.isBlank()) {
            return placeRepository.findAll();
        }
        return placeRepository.findByNameContainingIgnoreCaseOrAddressContainingIgnoreCaseOrTypeContainingIgnoreCase(query, query, query);
    }

    public Place create(PlaceRequest request) {
        Place place = new Place();
        place.setName(request.getName());
        place.setAddress(request.getAddress());
        place.setType(request.getType());
        place.setDescription(request.getDescription());
        place.setImageUrl(request.getImageUrl());
        return placeRepository.save(place);
    }

    public Place update(Long id, PlaceRequest request) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mesto ne postoji"));
        place.setName(request.getName());
        place.setAddress(request.getAddress());
        place.setType(request.getType());
        place.setDescription(request.getDescription());
        place.setImageUrl(request.getImageUrl());
        return placeRepository.save(place);
    }

    public void delete(Long id) {
        placeRepository.deleteById(id);
    }
}

package rs.projectues.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.projectues.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
	java.util.List<Place> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCaseOrTypeContainingIgnoreCase(String name, String address, String type);
}

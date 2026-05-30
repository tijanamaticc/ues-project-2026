package rs.projectues.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.projectues.entity.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByPlaceId(Long placeId);
    List<Event> findByEventDate(LocalDate eventDate);
}
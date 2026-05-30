package rs.projectues.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.projectues.entity.Event;
import rs.projectues.entity.Place;
import rs.projectues.entity.Role;
import rs.projectues.entity.User;
import rs.projectues.repository.EventRepository;
import rs.projectues.repository.PlaceRepository;
import rs.projectues.repository.UserRepository;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedData(UserRepository userRepository, PlaceRepository placeRepository, EventRepository eventRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@projectues.rs").isEmpty()) {
                User admin = new User();
                admin.setEmail("admin@projectues.rs");
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFirstName("Administrator");
                admin.setLastName("");
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }

            if (placeRepository.count() == 0) {
                Place place = new Place();
                place.setName("KC Svilara");
                place.setAddress("Petefi Šandora 3");
                place.setType("kultura");
                place.setDescription("Primer mesta za prikaz u aplikaciji.");
                place.setImageUrl("https://images.unsplash.com/photo-1529421308418-eab98863cee8");
                placeRepository.save(place);
            }

            if (eventRepository.count() == 0) {
                Place place = placeRepository.findAll().stream().findFirst().orElse(null);
                if (place != null) {
                    Event event = new Event();
                    event.setName("Veče jazz muzike");
                    event.setPlaceId(place.getId());
                    event.setPlaceName(place.getName());
                    event.setAddress(place.getAddress());
                    event.setType("muzika");
                    event.setEventDate(java.time.LocalDate.now());
                    event.setRecurring(false);
                    event.setFreeEntry(true);
                    event.setEntryPrice(0.0);
                    event.setDescription("Primer današnjeg događaja za prikaz i filtriranje.");
                    eventRepository.save(event);
                }
            }
        };
    }
}

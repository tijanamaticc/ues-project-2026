package rs.projectues.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.projectues.entity.RegistrationRequest;
import rs.projectues.entity.RequestStatus;
import java.util.List;

public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Long> {
    List<RegistrationRequest> findByStatus(RequestStatus status);
}

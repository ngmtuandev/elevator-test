package elevator.elevator.repository;

import elevator.elevator.entity.ElevatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ElevatorRepository extends JpaRepository<ElevatorEntity, UUID> {
}

package elevator.elevator.repository;

import elevator.elevator.entity.RequestUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequestUserRepository extends JpaRepository<RequestUserEntity, UUID> {
}

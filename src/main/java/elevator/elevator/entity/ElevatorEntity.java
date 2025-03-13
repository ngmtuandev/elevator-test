package elevator.elevator.entity;

import elevator.elevator.configurations.enums.EDirection;
import elevator.elevator.configurations.enums.EStatusElevator;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@Builder
public class ElevatorEntity extends BaseEntity{

    private int currentFloor;

    @Enumerated(EnumType.STRING)
    private EDirection direction;

    @Enumerated(EnumType.STRING)
    private EStatusElevator statusElevator;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> pendingFloors = new ArrayList<>();

    // RELATIONSHIP

    @OneToMany(mappedBy = "elevator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestUserEntity> requests = new ArrayList<>();

}

package elevator.elevator.entity;

import elevator.elevator.configurations.enums.EDirection;
import elevator.elevator.configurations.enums.EStatusElevator;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElevatorEntity extends BaseEntity{

    private int currentFloor;

    private int position;

    @Enumerated(EnumType.STRING)
    private EDirection direction;

    @Enumerated(EnumType.STRING)
    private EStatusElevator statusElevator;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> pendingFloors = new ArrayList<>();

}

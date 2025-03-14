package elevator.elevator.model.response.elevator;

import elevator.elevator.configurations.enums.EStatusElevator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ElevatorDTO {

    private UUID id;

    private int position;

    private int currentFloor;

    private String direction;

    private EStatusElevator statusElevator;

    private List<Integer> pendingFloors;

}

package elevator.elevator.model.request;

import elevator.elevator.configurations.enums.EDirection;
import elevator.elevator.configurations.enums.EStatusElevator;
import elevator.elevator.entity.ElevatorEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RequestCreateElevator {

    private int currentFloor;

    private EStatusElevator statusElevator;

    private EDirection direction;

    public ElevatorEntity toEntity() {
        return ElevatorEntity.builder()
                .currentFloor(this.currentFloor)
                .statusElevator(this.statusElevator)
                .direction(this.direction)
                .build();
    }


}

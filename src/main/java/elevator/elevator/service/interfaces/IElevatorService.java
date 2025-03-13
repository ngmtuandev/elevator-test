package elevator.elevator.service.interfaces;

import elevator.elevator.entity.ElevatorEntity;
import elevator.elevator.model.request.RequestCreateElevator;
import elevator.elevator.model.response.ResponseBase;

import java.util.UUID;

public interface IElevatorService {

    public ResponseBase createElevator(RequestCreateElevator requestCreateElevator);

    public ElevatorEntity findElevatorNearest(int floor, String direction);

    public void openDoor(UUID elevatorId);

    public void closeDoor(UUID elevatorId);

}

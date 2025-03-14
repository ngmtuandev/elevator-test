package elevator.elevator.service.interfaces;

import elevator.elevator.model.request.RequestCreateElevator;
import elevator.elevator.model.response.ResponseBase;
import elevator.elevator.model.response.ResponseBaseWithData;

import java.util.UUID;

public interface IElevatorService {

    public ResponseBase createElevator(RequestCreateElevator requestCreateElevator);

    public ResponseBaseWithData  findAll();

    public ResponseBaseWithData findElevatorNearest(int floor, String direction);

    public ResponseBaseWithData callSpecificElevator(UUID elevatorId, int floor);

    public ResponseBase moveElevators();

    public void openDoor(UUID elevatorId);

    public void closeDoor(UUID elevatorId);

}

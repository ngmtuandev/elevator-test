package elevator.elevator.controller;
import elevator.elevator.entity.ElevatorEntity;
import elevator.elevator.model.request.RequestCreateElevator;
import elevator.elevator.model.response.ResponseBase;
import elevator.elevator.model.response.ResponseBaseWithData;
import elevator.elevator.service.implement.ElevatorServiceImplement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("elevator")
@RequiredArgsConstructor
public class ElevatorController {

    private final ElevatorServiceImplement elevatorServiceImplement;

    @PostMapping()
    public ResponseBase create (@RequestBody RequestCreateElevator requestCreateElevator) {
        return this.elevatorServiceImplement.createElevator(requestCreateElevator);
    }

    @GetMapping()
    public ResponseBaseWithData get () {
        return this.elevatorServiceImplement.findAll();
    }

    @GetMapping("/find-elevator")
    public ResponseBaseWithData findNearestElevator(@RequestParam int floor, @RequestParam String direction) {
        return this.elevatorServiceImplement.findElevatorNearest(floor, direction);
    }

    @PostMapping("/call-specific")
    public ResponseBaseWithData callSpecificElevator(@RequestParam UUID elevatorId, @RequestParam int floor) {
        return this.elevatorServiceImplement.callSpecificElevator(elevatorId, floor);
    }

    @PostMapping("/move")
    public ResponseBase moveElevators() {
        return this.elevatorServiceImplement.moveElevators();
    }

    @PostMapping("/open-door")
    public void openDoor(@RequestParam UUID elevatorId) {
        this.elevatorServiceImplement.openDoor(elevatorId);
    }

    @PostMapping("/close-door")
    public void closeDoor(@RequestParam UUID elevatorId) {
        this.elevatorServiceImplement.closeDoor(elevatorId);
    }

}

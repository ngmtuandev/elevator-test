package elevator.elevator.controller;

import elevator.elevator.model.request.RequestCreateElevator;
import elevator.elevator.model.response.ResponseBase;
import elevator.elevator.service.implement.ElevatorServiceImplement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("elevator")
@RequiredArgsConstructor
public class ElevatorController {

    private final ElevatorServiceImplement elevatorServiceImplement;

    @PostMapping()
    public ResponseBase create (@RequestBody RequestCreateElevator requestCreateElevator) {
        ResponseBase response = this.elevatorServiceImplement.createElevator(requestCreateElevator);
        return  response;
    }

}

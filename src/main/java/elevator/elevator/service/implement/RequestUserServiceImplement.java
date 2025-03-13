package elevator.elevator.service.implement;

import elevator.elevator.repository.ElevatorRepository;
import elevator.elevator.repository.RequestUserRepository;
import elevator.elevator.service.interfaces.IRequestUserService;
import elevator.elevator.utils.RequestUserUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RequestUserServiceImplement implements IRequestUserService {

    private final RequestUserUtils requestUserUtils;
    private final ElevatorRepository elevatorRepository;
    private final RequestUserRepository requestUserRepository;

}

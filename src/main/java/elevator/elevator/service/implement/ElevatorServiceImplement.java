package elevator.elevator.service.implement;

import elevator.elevator.configurations.enums.ResourceBundleConstant;
import elevator.elevator.configurations.enums.SystemConstant;
import elevator.elevator.entity.ElevatorEntity;
import elevator.elevator.model.request.RequestCreateElevator;
import elevator.elevator.model.response.ResponseBase;
import elevator.elevator.repository.ElevatorRepository;
import elevator.elevator.repository.RequestUserRepository;
import elevator.elevator.service.interfaces.IElevatorService;
import elevator.elevator.utils.BaseAmenityUtil;
import elevator.elevator.utils.ElevatorUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class ElevatorServiceImplement implements IElevatorService {

    private final ElevatorUtils elevatorUtils;
    private final ElevatorRepository elevatorRepository;
    private final RequestUserRepository requestUserRepository;
    private final BaseAmenityUtil baseAmenityUtil;

    private String getMessageBundle(String key) {
        return baseAmenityUtil.getMessageBundle(key);
    }


    @Override
    public ResponseBase createElevator(RequestCreateElevator requestCreateElevator) {
        try {
            ElevatorEntity newElevator = requestCreateElevator.toEntity();
            this.elevatorRepository.save(newElevator);
            return new ResponseBase(ResourceBundleConstant.ELEVATOR_01, getMessageBundle(ResourceBundleConstant.ELEVATOR_01), SystemConstant.STATUS_CODE_SUCCESS);
        } catch (Exception e) {
            return new ResponseBase(ResourceBundleConstant.ELEVATOR_04, getMessageBundle(ResourceBundleConstant.ELEVATOR_04), SystemConstant.STATUS_CODE_BAD_REQUEST);
        }
    }

    public ElevatorEntity findElevatorNearest(int floor, String direction) {
        return null;
    }

    @Override
    public void openDoor(UUID elevatorId) {

    }

    @Override
    public void closeDoor(UUID elevatorId) {

    }


}

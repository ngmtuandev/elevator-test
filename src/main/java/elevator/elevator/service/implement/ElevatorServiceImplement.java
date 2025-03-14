package elevator.elevator.service.implement;

import elevator.elevator.configurations.enums.*;
import elevator.elevator.entity.ElevatorEntity;
import elevator.elevator.model.request.RequestCreateElevator;
import elevator.elevator.model.response.ResponseBase;
import elevator.elevator.model.response.ResponseBaseWithData;
import elevator.elevator.model.response.elevator.ElevatorDTO;
import elevator.elevator.repository.ElevatorRepository;
import elevator.elevator.service.interfaces.IElevatorService;
import elevator.elevator.utils.BaseAmenityUtil;
import elevator.elevator.utils.ElevatorNotFoundException;
import elevator.elevator.utils.ElevatorUtils;
import elevator.elevator.utils.WebSocketHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ElevatorServiceImplement implements IElevatorService {

    private final ElevatorUtils elevatorUtils;
    private final ElevatorRepository elevatorRepository;
    private final BaseAmenityUtil baseAmenityUtil;
    private final WebSocketHandler webSocketHandler;

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

    @Override
    public ResponseBaseWithData findAll() {
            List<ElevatorEntity> elevators = elevatorRepository.findAll();

            List<ElevatorDTO> elevatorDTOs = elevators.stream()
                    .map(e -> new ElevatorDTO(
                            e.getId(),
                            e.getPosition(),
                            e.getCurrentFloor(),
                            e.getDirection().name(),
                            e.getStatusElevator(),
                            e.getPendingFloors()
                    ))
                    .collect(Collectors.toList());

            return new ResponseBaseWithData(
                    ResourceBundleConstant.ELEVATOR_07,
                    getMessageBundle(ResourceBundleConstant.ELEVATOR_07),
                    SystemConstant.STATUS_CODE_SUCCESS,
                    elevatorDTOs
            );
    }

    @Override
    public ResponseBase moveElevators() {
        List<ElevatorEntity> elevators = elevatorRepository.findAll();
        boolean moved = false;

        for (ElevatorEntity elevator : elevators) {
            if (!elevator.getPendingFloors().isEmpty()) {
                int nextFloor = elevator.getPendingFloors().remove(0);
                elevator.setCurrentFloor(nextFloor);

                log.info("Thang máy {} được di chuyển tơới tầng {}", elevator.getPosition(), nextFloor);
                moved = true;

                if (elevator.getPendingFloors().isEmpty()) {
                    elevator.setStatusElevator(EStatusElevator.IDLE);
                    elevator.setDirection(EDirection.NONE);
                }

                elevatorRepository.save(elevator);
            }
        }

        if (!moved) {
            return new ResponseBase(
                    ResourceBundleConstant.NO_MOVEMENT,
                    getMessageBundle(ResourceBundleConstant.NO_MOVEMENT),
                    SystemConstant.STATUS_CODE_SUCCESS
            );
        }
        if (moved) {
            webSocketHandler.broadcastMessage(elevatorRepository.findAll());
        }

        return new ResponseBase(
                ResourceBundleConstant.ELEVATORS_MOVED,
                getMessageBundle(ResourceBundleConstant.ELEVATORS_MOVED),
                SystemConstant.STATUS_CODE_SUCCESS
        );
    }

    public ResponseBaseWithData findElevatorNearest(int floor, String direction) {
        List<ElevatorEntity> elevators = elevatorRepository.findAll();

        // Ưu tiên thang máy gần nhất + đúng hướng
        Optional<ElevatorEntity> bestElevator = elevators.stream()
                .filter(e -> e.getDirection().name().equals(direction) &&
                        e.getPendingFloors().contains(floor))
                .min(Comparator.comparingInt(e -> Math.abs(e.getCurrentFloor() - floor)));

        if (bestElevator.isPresent()) {
            return new ResponseBaseWithData(
                    ResourceBundleConstant.ELEVATOR_06,
                    getMessageBundle(ResourceBundleConstant.ELEVATOR_06),
                    SystemConstant.STATUS_CODE_SUCCESS,
                    bestElevator.get()
            );
        }

        Optional<ElevatorEntity> idleElevator = elevators.stream()
                .filter(e -> e.getStatusElevator() == EStatusElevator.IDLE)
                .min(Comparator.comparingInt(e -> Math.abs(e.getCurrentFloor() - floor)));

        if (idleElevator.isPresent()) {
            return new ResponseBaseWithData(
                    ResourceBundleConstant.ELEVATOR_06,
                    getMessageBundle(ResourceBundleConstant.ELEVATOR_06),
                    SystemConstant.STATUS_CODE_SUCCESS,
                    idleElevator.get()
            );
        }

        return new ResponseBaseWithData(
                ResourceBundleConstant.ELEVATOR_05,
                "Không có thang máy trống để phục vụ!",
                SystemConstant.STATUS_CODE_BAD_REQUEST,
                null
        );
    }

    @Override
    public ResponseBaseWithData callSpecificElevator(UUID elevatorId, int floor) {
        ElevatorEntity elevator = elevatorRepository.findById(elevatorId)
                .orElseThrow(() -> new ElevatorNotFoundException("Thang máy không tồn tại!"));

        if (elevator.getDirection() == EDirection.NONE) {
            elevator.setDirection(floor > elevator.getCurrentFloor() ? EDirection.UP : EDirection.DOWN);
        }

        elevator.getPendingFloors().add(floor);
        elevator.setStatusElevator(EStatusElevator.MOVING);
        elevatorRepository.save(elevator);

        return new ResponseBaseWithData(
                "ELEVATOR_CALLED",
                "Thang máy " + elevator.getPosition() + " đang đến tầng " + floor,
                SystemConstant.STATUS_CODE_SUCCESS,
                elevator
        );
    }

    @Override
    public void openDoor(UUID elevatorId) {
        ElevatorEntity findElevator = elevatorRepository.findById(elevatorId)
                .orElseThrow(() -> new ElevatorNotFoundException("Thang máy không tồn tại!"));

        elevatorRepository.findById(elevatorId).ifPresent(elevator -> {
            log.info("Thang máy {} đang mở cửa", findElevator.getPosition());
            try {
                Thread.sleep(10000); // 10 giây
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thang máy {} mowr cửa", findElevator.getPosition(), e);
            }
        });
    }

    @Override
    public void closeDoor(UUID elevatorId) {
        ElevatorEntity findElevator = elevatorRepository.findById(elevatorId)
                .orElseThrow(() -> new ElevatorNotFoundException("Thang máy không tồn tại!"));
        elevatorRepository.findById(elevatorId).ifPresent(elevator -> {
            log.info("Thang máy {} đóng cửa",  findElevator.getPosition());
        });
    }


}

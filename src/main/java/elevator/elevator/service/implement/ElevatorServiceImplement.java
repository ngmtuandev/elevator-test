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
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
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
    @Transactional
    public ResponseBase moveElevators() {
        List<ElevatorEntity> elevators = elevatorRepository.findAll();
        boolean moved = false;

        for (ElevatorEntity elevator : elevators) {
            if (!elevator.getPendingFloors().isEmpty()) {
                int nextFloor = elevator.getPendingFloors().remove(0);
                elevator.setCurrentFloor(nextFloor);
                elevator.setStatusElevator(EStatusElevator.DOOR_OPENING);
                elevatorRepository.save(elevator);

                log.info("Thang máy {} đến tầng {}, mở cửa", elevator.getPosition(), nextFloor);

                // Gửi cập nhật qua WebSocket
                webSocketHandler.broadcastMessage(elevatorRepository.findAll());

                // Giữ cửa mở 5 giây rồi đóng cửa
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        closeDoor(elevator.getId());
                    }
                }, 5000);

                moved = true;
            }
        }

        return new ResponseBase(
                "ELEVATORS_MOVED",
                "Thang máy đã di chuyển",
                SystemConstant.STATUS_CODE_SUCCESS
        );
    }

    @Override
    public ResponseBaseWithData findElevatorNearest(int floor, String direction) {
        List<ElevatorEntity> elevators = elevatorRepository.findAll();

        // Ưu tiên thang máy đã có hướng đi phù hợp
        Optional<ElevatorEntity> bestElevator = elevators.stream()
                .filter(e -> e.getDirection().name().equals(direction) &&
                        ((direction.equals("UP") && e.getCurrentFloor() <= floor) ||
                                (direction.equals("DOWN") && e.getCurrentFloor() >= floor)))
                .min(Comparator.comparingInt(e -> Math.abs(e.getCurrentFloor() - floor)));

        if (bestElevator.isPresent()) {
            return new ResponseBaseWithData(
                    "ELEVATOR_FOUND",
                    "Thang máy được tìm thấy",
                    SystemConstant.STATUS_CODE_SUCCESS,
                    bestElevator.get()
            );
        }

        // Nếu không có thang máy nào đúng hướng, tìm thang máy đang IDLE
        Optional<ElevatorEntity> idleElevator = elevators.stream()
                .filter(e -> e.getStatusElevator() == EStatusElevator.IDLE)
                .min(Comparator.comparingInt(e -> Math.abs(e.getCurrentFloor() - floor)));

        if (idleElevator.isPresent()) {
            return new ResponseBaseWithData(
                    "ELEVATOR_IDLE_FOUND",
                    "Thang máy đang rảnh được chọn",
                    SystemConstant.STATUS_CODE_SUCCESS,
                    idleElevator.get()
            );
        }

        return new ResponseBaseWithData(
                "ELEVATOR_NOT_FOUND",
                "Không có thang máy phù hợp!",
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
        ElevatorEntity elevator = elevatorRepository.findById(elevatorId)
                .orElseThrow(() -> new ElevatorNotFoundException("Thang máy không tồn tại!"));

        log.info("Thang máy {} đang mở cửa", elevator.getPosition());
        elevator.setStatusElevator(EStatusElevator.DOOR_OPENING);
        elevatorRepository.save(elevator);

        // Giữ cửa mở 5 giây rồi đóng cửa
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                closeDoor(elevatorId);
            }
        }, 5000);
    }

    @Override
    public void closeDoor(UUID elevatorId) {
        ElevatorEntity elevator = elevatorRepository.findById(elevatorId)
                .orElseThrow(() -> new ElevatorNotFoundException("Thang máy không tồn tại!"));

        log.info("Thang máy {} đóng cửa", elevator.getPosition());
        elevator.setStatusElevator(EStatusElevator.IDLE);
        elevatorRepository.save(elevator);
    }


}

package elevator.elevator.utils;

public class ElevatorNotFoundException extends RuntimeException {
    public ElevatorNotFoundException(String message) {
        super(message);
    }
}
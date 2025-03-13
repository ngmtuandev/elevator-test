package elevator.elevator.entity;

import elevator.elevator.configurations.enums.EDirection;
import elevator.elevator.configurations.enums.EStatusRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data
public class RequestUserEntity extends BaseEntity{

    private EDirection direction;

    private int floorRequest;

    private EStatusRequest statusRequest;

    // RELATIONSHIP

    @ManyToOne
    @JoinColumn(name = "elevator_id", nullable = false)
    private ElevatorEntity elevator;

}

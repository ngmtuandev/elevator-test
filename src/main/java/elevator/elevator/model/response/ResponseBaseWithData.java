package elevator.elevator.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResponseBaseWithData {

    String code;

    String message;

    int status;

    Object data;

}

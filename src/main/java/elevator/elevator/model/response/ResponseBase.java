package elevator.elevator.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResponseBase {

    String code;
    String message;
    int status;

}

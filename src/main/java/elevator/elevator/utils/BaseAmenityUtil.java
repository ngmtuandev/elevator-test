package elevator.elevator.utils;

import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
public class BaseAmenityUtil {
    public String getMessageBundle(String key) {

        ResourceBundle resourceBundle = ResourceBundle.getBundle("elevator");
        return resourceBundle.getString(key);
    }
}

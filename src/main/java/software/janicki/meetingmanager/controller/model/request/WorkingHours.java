package software.janicki.meetingmanager.controller.model.request;

import java.time.LocalTime;

public record WorkingHours(
        LocalTime begin,
        LocalTime end
) {
}

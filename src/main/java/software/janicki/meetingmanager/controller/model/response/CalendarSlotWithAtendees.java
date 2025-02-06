package software.janicki.meetingmanager.controller.model.response;

import java.time.LocalDateTime;
import java.util.List;

public record CalendarSlotWithAtendees(
        LocalDateTime begin,
        LocalDateTime end,
        List<String> attendees
) {
}

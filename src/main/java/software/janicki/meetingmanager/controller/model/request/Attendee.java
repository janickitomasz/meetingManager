package software.janicki.meetingmanager.controller.model.request;

import java.time.ZoneId;
import java.util.List;

public record Attendee(
        String name,
        List<CalendarSlot> bookedSlots,
        WorkingHours workingHours,
        String zoneId
) {
}

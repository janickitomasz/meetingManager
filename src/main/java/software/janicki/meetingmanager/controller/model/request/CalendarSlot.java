package software.janicki.meetingmanager.controller.model.request;

import java.time.LocalDateTime;
import java.util.Objects;

public record CalendarSlot(
        LocalDateTime start,
        LocalDateTime end
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarSlot that)) return false;
        return end.isEqual(that.end) && start.isEqual(that.start);
    }

    @Override
    public int hashCode() {
        return start.getHour()+start.getMinute()+end.getHour()+end.getMinute();
    }
}

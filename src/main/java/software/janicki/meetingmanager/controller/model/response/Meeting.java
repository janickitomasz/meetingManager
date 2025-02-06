package software.janicki.meetingmanager.controller.model.response;


import java.time.LocalDateTime;
import java.util.List;

public record Meeting(
    LocalDateTime start,
    LocalDateTime end,
    List<String> attendees
){

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Meeting that)) return false;
        return end.isEqual(that.end) && start.isEqual(that.start);
    }

    @Override
    public int hashCode() {
        return start.getHour()+start.getMinute()+end.getHour()+end.getMinute();
    }
}

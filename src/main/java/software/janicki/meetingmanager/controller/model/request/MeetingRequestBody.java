package software.janicki.meetingmanager.controller.model.request;

import java.time.LocalDateTime;
import java.util.List;

public record MeetingRequestBody(
    List<Attendee> attendees,
    LocalDateTime windowStart,
    LocalDateTime windowEnd,
    int numberOfMeetings,
    int meetingLength
) {
}

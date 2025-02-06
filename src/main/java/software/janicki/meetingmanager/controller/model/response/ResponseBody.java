package software.janicki.meetingmanager.controller.model.response;

import java.util.List;

public record ResponseBody(
        List<Meeting> meetingList,
        String message
) {
}

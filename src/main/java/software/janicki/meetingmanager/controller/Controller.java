package software.janicki.meetingmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.janicki.meetingmanager.controller.model.request.MeetingRequestBody;
import software.janicki.meetingmanager.controller.model.response.Meeting;
import software.janicki.meetingmanager.controller.model.response.ResponseBody;
import software.janicki.meetingmanager.service.MeetingService;

import java.util.List;

@RestController("/")
public class Controller {

private MeetingService meetingService;

@Autowired
public Controller(MeetingService meetingService) {
    this.meetingService = meetingService;
}

@PostMapping("/fullMeeting")
public ResponseBody getSlotsForFullMeetings(@RequestBody MeetingRequestBody requestBody) {
       List<Meeting> response = meetingService.getFullMeetingsSlot(requestBody);
       if (response.isEmpty()) {
           return new ResponseBody(null, "Meeting not found for given criteria");
       }
       return new ResponseBody(response, "Meetings found for given criteria");
}

@PostMapping("/bestMeeting")
public List<Meeting> getBestMeeting(@RequestBody MeetingRequestBody requestBody){
    return meetingService.getBestMeetings(requestBody);
}

}

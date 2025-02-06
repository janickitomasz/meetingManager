package software.janicki.meetingmanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import software.janicki.meetingmanager.controller.model.request.Attendee;
import software.janicki.meetingmanager.controller.model.request.CalendarSlot;
import software.janicki.meetingmanager.controller.model.request.MeetingRequestBody;
import software.janicki.meetingmanager.controller.model.response.CalendarSlotWithAtendees;
import software.janicki.meetingmanager.controller.model.response.Meeting;
import software.janicki.meetingmanager.service.model.AtendeeCalendar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;


@Service
public class MeetingService {
    @Value("${global.slotSize}")
    int slotSize;

    @Value("${global.timezone}")
    ZoneId zoneId;


    public List<Meeting> getFullMeetingsSlot(MeetingRequestBody meetingRequestBody){
        List<AtendeeCalendar> attendeesCalendars = new ArrayList<>();
        List<String> attendees = new ArrayList<>();

        geAttendeeData(meetingRequestBody, attendeesCalendars, attendees);

        List<CalendarSlot> calendarSlots = null;

        for(AtendeeCalendar attendeeCalendar:attendeesCalendars){
            if(calendarSlots == null){
                calendarSlots = attendeeCalendar.getCalendarSlots();
            }else{
                calendarSlots.retainAll(attendeeCalendar.getCalendarSlots());
            }

        }

        int meetingLength = meetingRequestBody.meetingLength();

        List<Meeting> meetingSlots = new ArrayList<>();

        CalendarSlot tmpSlot = null;
        LocalDateTime tmpStart = null;
        int tmpLength = 0;

        for(CalendarSlot calendarSlot:calendarSlots){
            if(tmpSlot == null){
                tmpSlot = calendarSlot;
                tmpStart = calendarSlot.start();
                tmpLength = 1;
            }else{
                if(tmpSlot.end().equals(calendarSlot.start())){
                    tmpLength++;
                    tmpSlot = calendarSlot;
                }else{
                    tmpSlot=calendarSlot;
                    tmpLength = 1;
                    tmpStart = calendarSlot.start();
                }
            }
            if(tmpLength == meetingLength){
                meetingSlots.add(new Meeting(tmpStart,tmpSlot.end(),attendees));
                tmpSlot = null;
                tmpStart = null;
            }

        }

        return meetingSlots.stream().limit(meetingRequestBody.numberOfMeetings()).toList();
    }



    public List<Meeting> getBestMeetings(MeetingRequestBody meetingRequestBody){
        List<AtendeeCalendar> attendeesCalendars = new ArrayList<>();
        List<String> attendees = new ArrayList<>();

        geAttendeeData(meetingRequestBody, attendeesCalendars, attendees);

        LocalDateTime startDateTime = meetingRequestBody.windowStart().atZone(zoneId).toLocalDateTime();
        LocalDateTime stoptDateTime = meetingRequestBody.windowEnd().atZone(zoneId).toLocalDateTime();
        LocalDateTime tmpDateTime = startDateTime;

        int meetingsNumber = meetingRequestBody.numberOfMeetings();
        int meetingLength = meetingRequestBody.meetingLength();
        int meetingLengthMinutes = meetingLength * slotSize;

        int foundMeetings = 0;;
        PriorityQueue<Map.Entry<Meeting, Integer>> bestMeetings = new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry<Meeting, Integer>::getValue).reversed()
        );

        while(!tmpDateTime.plusMinutes(meetingLengthMinutes).isAfter(stoptDateTime) && meetingsNumber > foundMeetings){

            List<String> tmpAttendees = getMeetingPossibleAttendees(meetingLength, tmpDateTime, attendeesCalendars);

            Meeting calendarSlotWithAtendees = new Meeting(tmpDateTime, tmpDateTime.plusMinutes(meetingLengthMinutes), tmpAttendees);
            int size = tmpAttendees.size();

            if(size>1){
                bestMeetings.add(Map.entry(calendarSlotWithAtendees, size));
            }
            if(size==meetingRequestBody.attendees().size()){
                foundMeetings++;
                tmpDateTime = tmpDateTime.plusMinutes(meetingLengthMinutes);
            }else{
                tmpDateTime=tmpDateTime.plusMinutes(slotSize);
            }


        }
        return selectMeetings(bestMeetings).stream().limit(meetingsNumber).toList();
    }

    private List<String> getMeetingPossibleAttendees(int meetingLength, LocalDateTime tmpDateTimeForMeeting, List<AtendeeCalendar> attendeesCalendars) {
        List<String> tmpAttendees = null;
        int tmplLength=0;
        while(tmplLength < meetingLength){
            if(tmpAttendees == null){
                tmpAttendees = getAvailableAttendees(tmpDateTimeForMeeting, attendeesCalendars);
            }else{
                tmpAttendees.retainAll(getAvailableAttendees(tmpDateTimeForMeeting, attendeesCalendars));
            }
            tmpDateTimeForMeeting = tmpDateTimeForMeeting.plusMinutes(slotSize);
            tmplLength++;

        }
        return tmpAttendees;
    }

    private List<Meeting> selectMeetings(PriorityQueue<Map.Entry<Meeting, Integer>> bestMeetings) {
        List<Meeting> selectedMeetings = new ArrayList<Meeting>();

        for(Meeting meeting:bestMeetings.stream().map(e -> e.getKey()).toList()){
            if(!isincluded(meeting, selectedMeetings)) selectedMeetings.add(meeting);
        }

        return selectedMeetings;
    }

    private boolean isincluded(Meeting meetingToCheck, List<Meeting> selectedMeetings) {
        for(Meeting meeting:selectedMeetings){
            if(meetingToCheck.start().isAfter(meeting.start()) && meetingToCheck.start().isBefore(meeting.end())){
                return true;
            }
            if(meetingToCheck.end().isAfter(meeting.start()) && meetingToCheck.end().isBefore(meeting.end())){
                return true;
            }
        }
        return false;
    }

    private List<String> getAvailableAttendees(LocalDateTime tmpDateTimeForMeeting, List<AtendeeCalendar> attendeesCalendars) {
        List<String> result = new ArrayList<>();
        for(AtendeeCalendar attendeeCalendar:attendeesCalendars){
            if(isAttendeeAvailable(tmpDateTimeForMeeting, attendeeCalendar)){
                result.add(attendeeCalendar.getName());
            }
        }
        return result;
    }

    private boolean isAttendeeAvailable(LocalDateTime tmpDateTimeForMeeting, AtendeeCalendar attendeeCalendar) {
        return attendeeCalendar.getCalendarSlots().contains(new CalendarSlot(tmpDateTimeForMeeting, tmpDateTimeForMeeting.plusMinutes(slotSize)));
    }

    private void geAttendeeData(MeetingRequestBody meetingRequestBody, List<AtendeeCalendar> attendeesCalendars, List<String> attendees) {
        for(Attendee attendee: meetingRequestBody.attendees()){
            attendeesCalendars.add(new AtendeeCalendar(attendee, meetingRequestBody.windowStart(), meetingRequestBody.windowEnd() ,slotSize, zoneId));
            attendees.add(attendee.name().toString());
        }
    }
}

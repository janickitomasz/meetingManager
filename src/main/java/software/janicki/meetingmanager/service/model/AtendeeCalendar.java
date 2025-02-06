package software.janicki.meetingmanager.service.model;

import org.springframework.beans.factory.annotation.Value;
import software.janicki.meetingmanager.controller.model.request.Attendee;
import software.janicki.meetingmanager.controller.model.request.CalendarSlot;
import software.janicki.meetingmanager.controller.model.request.WorkingHours;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class AtendeeCalendar {

    String name;
    List<CalendarSlot> calendarSlots = new ArrayList<>();
    ZoneId zoneId;

    public AtendeeCalendar(Attendee attendee, LocalDateTime windowStart, LocalDateTime windowEnd, int slotSize, ZoneId zoneId){
        this.zoneId= zoneId;
        this.name = attendee.name();

        ZoneId attendeeZoneId = ZoneId.of(attendee.zoneId());
        ZonedDateTime tmpDateTime = windowStart.atZone(zoneId);

        while(tmpDateTime.isBefore(windowEnd.atZone(zoneId)) ){
            ZonedDateTime attendeeTmpDateTime = tmpDateTime.withZoneSameInstant(attendeeZoneId);
                if(isTimeInWorkingHours(attendeeTmpDateTime.toLocalTime(), attendee.workingHours(), slotSize) ){
                    calendarSlots.add(new CalendarSlot(tmpDateTime.toLocalDateTime(), tmpDateTime.toLocalDateTime().plusMinutes(slotSize)));
                }


            tmpDateTime = tmpDateTime.plusMinutes(slotSize);
        }

        calendarSlots.removeAll(attendee.bookedSlots());
        System.out.println(calendarSlots);
    }

    public String getName() {
        return name;
    }

    public List<CalendarSlot> getCalendarSlots() {
        return calendarSlots;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    static boolean isTimeInWorkingHours(LocalTime time, WorkingHours workingHours, int slotSize){
        if(time.isBefore(workingHours.begin()) || time.isAfter(workingHours.end()) ) return false;
        if(time.plusMinutes(slotSize).isAfter(workingHours.end())) return false;
        return true;
    }

    static CalendarSlot translateCalendarSlot(CalendarSlot calendarSlotIn, ZoneId zoneIdToTranslate, ZoneId zoneId){
        LocalDateTime start = calendarSlotIn.start().atZone(zoneIdToTranslate).withZoneSameInstant(zoneId).toLocalDateTime();
        LocalDateTime end = calendarSlotIn.end().atZone(zoneIdToTranslate).withZoneSameInstant(zoneId).toLocalDateTime();
        return new CalendarSlot(start,end);
    }
}

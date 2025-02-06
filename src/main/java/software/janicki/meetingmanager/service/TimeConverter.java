package software.janicki.meetingmanager.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeConverter {
    public static ZonedDateTime convertTime(LocalDateTime time, ZoneId zoneIdFrom, ZoneId zoneIdTo) {
        return ZonedDateTime.of(time, zoneIdFrom).withZoneSameInstant(zoneIdTo);
    }
}

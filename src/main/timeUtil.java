package main;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class timeUtil {
    private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("MM/dd/yyyy'T'HH:mm:ss:SSS z");

    /*
        Parameter should be EST time zoneDateTime object
     */
    public static Timestamp convertTimeDateUTC(ZonedDateTime dateTime) {
        ZonedDateTime utcZoneDT = dateTime.toInstant().atZone(ZoneId.of("UTC"));
        Timestamp dbDate = Timestamp.from(utcZoneDT.toInstant());
        return dbDate;
    }

    public static LocalDateTime convertTimeDateLocal(ZonedDateTime dateTime) {
        ZonedDateTime utcZoneDT = dateTime.toInstant().atZone(ZoneId.systemDefault());
        return utcZoneDT.toLocalDateTime();
    }

    public static  ZonedDateTime convertTimeDateEST(String dateTime) {
        Timestamp currentTimeStamp = Timestamp.valueOf(String.valueOf(dateTime));
        LocalDateTime localDT = currentTimeStamp.toLocalDateTime();
        ZonedDateTime zoneDT = localDT.atZone(ZoneId.systemDefault());
        LocalDateTime estDT = LocalDateTime.ofInstant(zoneDT.toInstant(), ZoneId.of("America/New_York"));
        ZonedDateTime estZoneDT = estDT.atZone(ZoneId.of("America/New_York"));

        return estZoneDT;


   }
}

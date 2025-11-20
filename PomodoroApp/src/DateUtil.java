import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateUtil {

    public static int getWeek(LocalDate d) {
        WeekFields wf = WeekFields.of(Locale.KOREA);
        return d.get(wf.weekOfYear());
    }

    public static int getMonth(LocalDate d) {
        return d.getMonthValue();
    }
}

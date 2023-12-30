import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Asistencia {

    public static String fechaHoy() {
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        return fechaActual.format(formatter);
    }
}

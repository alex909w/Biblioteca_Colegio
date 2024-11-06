package utilidades;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JFormattedTextField.AbstractFormatter;

public class DateLabelFormatter extends AbstractFormatter {

    private String datePattern = "yyyy-MM-dd";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parse(text); // Convierte una cadena en Date
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            if (value instanceof Date) { 
                // Si el valor es Date, formatea directamente
                return dateFormatter.format((Date) value);
            } else if (value instanceof Calendar) {
                // Si el valor es Calendar, convierte a Date primero
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
        }
        return "";
    }
}

package cz.destil.glasquare.util;

import java.text.DateFormat;
import java.util.Date;

/**
 * Utils for formatting.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class FormatUtils {
    public static String formatDate(Date date) {
        return DateFormat.getDateInstance().format(date);
    }

	public static String formatDateTime() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date());
	}
}

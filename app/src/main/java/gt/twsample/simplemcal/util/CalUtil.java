package gt.twsample.simplemcal.util;

import java.util.Calendar;

/**
 * Created by takashi.watanabe on 2017/02/09.
 */

public class CalUtil {
    public static final Calendar updateCalToNoon(Calendar c) {
        if(c == null){ return c; }
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }
}

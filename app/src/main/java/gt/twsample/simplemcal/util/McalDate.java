package gt.twsample.simplemcal.util;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by takashi.watanabe on 2017/02/07.
 */

public class McalDate {
    private static final String[] TZOLKIN_DATE = {
            "Imix", "I'k", "Ak'bal", "Kan", "Chickchan",
            "Kimi", "Manik'", "Lamat", "Muluk", "Ok",
            "Chuwen", "Eb", "Ben", "Ix", "Men",
            "Kib", "Kaban", "Etz'nab", "Kawak", "Ajaw"};
    private static final int[] TZOLKIN_MONTH = {1,2,3,4,5,6,7,8,9,10,11,12,13};
    private static final String[] WEEK_DAYS = {
            "Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
    private static final double JULIAN_CONSTANT = 2440587; // 2440587.5
    private static final long GMT_584283 = 584283;
    private static final long GMT_584285 = 584285;
    private static final int BAKTUN_MAX = 13;
    private static final long WINAL_BASE  = 20;
    private static final long TUN_BASE    = 18 * WINAL_BASE;
    private static final long KATUN_BASE  = 20 * TUN_BASE;
    private static final long BAKTUN_BASE = 20 * KATUN_BASE;
    private static final long PIKTUN_BASE = BAKTUN_MAX * BAKTUN_BASE;
    private static final long TZOLKIN_MONTH_HOSEI = 3;
    private static final long TZOLKIN_DATE_HOSEI = 19;

    private Calendar cal;
    private double jd;
    private long gmt;
    private MessageFormat long_count_format = new MessageFormat("{0}.{1}.{2}.{3}.{4}");
    private MessageFormat tzolkin_format = new MessageFormat("{0}-{1}");

    public McalDate(Calendar cal){
        CalUtil.updateCalToNoon(cal);
        this.cal = cal;
        long milli_time = cal.getTimeInMillis();
        this.jd = (Math.ceil(milli_time/(24.0*60*60*1000)) + JULIAN_CONSTANT);
        this.gmt = GMT_584283;
    }

    public void gmt_584283(){ gmt = GMT_584283; }
    public void gmt_584285(){ gmt = GMT_584285; }

    public String toLongCount(){
        final long base  = (long)jd - gmt;
        final int piktun = (int)(base / PIKTUN_BASE);
        long baktun      = (int)(base % PIKTUN_BASE / BAKTUN_BASE);
        final int katun  = (int)(base % BAKTUN_BASE / KATUN_BASE);
        final int tun    = (int)(base % KATUN_BASE / TUN_BASE);
        final int winal  = (int)(base % TUN_BASE / WINAL_BASE);
        final int kin    = (int)(base % WINAL_BASE);
        if(piktun == 1){ baktun = BAKTUN_MAX; }
        return long_count_format.format(new Object[]{baktun, katun, tun, winal, kin});
        // return String.format("%d.%d.%d.%d.%d", baktun, katun, tun, winal, kin);
    }

    public void previous(int added) {
        this.update(-added);
    }

    public void next(int added) {
        this.update(added);
    }

    public String toTzolkin(){
        final long base = (long)jd - gmt;
        final int tmonth = TZOLKIN_MONTH[(int)((base + TZOLKIN_MONTH_HOSEI) % TZOLKIN_MONTH.length)];
        final String tdate  = TZOLKIN_DATE[(int)((base + TZOLKIN_DATE_HOSEI) % TZOLKIN_DATE.length)];
        return tzolkin_format.format(new Object[]{tmonth, tdate});
        // return String.format("%d-%s", tmonth, tdate);
    }

    public String toGDate() {
        final String wday = WEEK_DAYS[cal.get(Calendar.DAY_OF_WEEK)-1];
        return String.format(Locale.US, "%d-%d-%d(%s)",
               cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE), wday);
        //SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd(E)", new Locale("es"));
        //return f.format(cal.getTime());
    }
    public String toMDate() {
        StringBuilder sb = new StringBuilder();
        return sb.append(this.toLongCount()).append(" : ").append(this.toTzolkin()).toString();
    }

    public void updateBaseDate(Calendar cal){
        this.cal = cal;
        update(0);
    }

    public void updateBaseDate(int year, int month, int date){
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, date);
        update(0);
    }

    public void updateBaseYear(int year){
        cal.set(Calendar.YEAR, year);
        update(0);
    }

    public Calendar getCal() {
        return cal;
    }

    private void update(int cnt) {
        cal.add(Calendar.DATE, cnt);
        final long milli_time = cal.getTimeInMillis();
        this.jd = (Math.ceil(milli_time/(24.0*60*60*1000)) + JULIAN_CONSTANT);
    }

    public static String toTzolkin(Calendar cal) {
        // TODO implement
       return "";
    }
    public static String toLongCount(Calendar cal) {
        // TODO implement
        return "";
    }
}

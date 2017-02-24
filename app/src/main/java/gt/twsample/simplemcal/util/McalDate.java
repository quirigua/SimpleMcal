package gt.twsample.simplemcal.util;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
    private static final String[] HAAB_MONTH = {
            "Pop", "Wo", "Sip", "Sots", "Sek", "Xul",
            "Yaxk'in", "Mol", "Che'n", "Yax", "Sak", "Keh",
            "Mak", "K'ank'in", "Muwan", "Pax", "K'ayab", "Kumk'u"};
    private static final int[] HAAB_DAYS = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
    private static final int[] WAYEB_DAYS = {1,2,3,4,5};
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
    private static final long HAAB_DAY_COUNT = 365;

    private Calendar cal;
    private double jd;
    private long gmt;
    private MessageFormat long_count_format = new MessageFormat("{0}.{1}.{2}.{3}.{4}");
    private MessageFormat tzolkin_format = new MessageFormat("{0}-{1}");
    private MessageFormat haab_format    = new MessageFormat("{0}-{1}");

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
        final int[] long_count = toLongCountAsArray();
        // piktunは表示せず、それ以外の5つを文字列整形して返す
        return long_count_format.format(
                new Object[]{long_count[1], long_count[2], long_count[3],
                        long_count[4], long_count[5]});
    }

    public int[] toLongCountAsArray() {
        final long base  = (long)jd - gmt;
        final int piktun = (int)(base / PIKTUN_BASE);
        int baktun       = (int)(base % PIKTUN_BASE / BAKTUN_BASE);
        final int katun  = (int)(base % BAKTUN_BASE / KATUN_BASE);
        final int tun    = (int)(base % KATUN_BASE / TUN_BASE);
        final int winal  = (int)(base % TUN_BASE / WINAL_BASE);
        final int kin    = (int)(base % WINAL_BASE);
        if(piktun == 1){ baktun = BAKTUN_MAX; }
        return new int[]{piktun, baktun, katun, tun, winal, kin};
    }

    public void previous_one() { this.update(-1); }
    public void next_one() { this.update(1); }

    public String toTzolkin(){
        final long base = (long)jd - gmt;
        final int tmonth = TZOLKIN_MONTH[(int)((base + TZOLKIN_MONTH_HOSEI) % TZOLKIN_MONTH.length)];
        final String tdate  = TZOLKIN_DATE[(int)((base + TZOLKIN_DATE_HOSEI) % TZOLKIN_DATE.length)];
        return tzolkin_format.format(new Object[]{tmonth, tdate});
    }

    public String toHaab() {
        final long base = (long)jd - gmt + 347;
        final int day_position = (int)(base % HAAB_DAY_COUNT);
        String hmonth = null;
        int hday = 0;
        if(day_position >= 360) {
            //WAYEB
            hmonth = "Wayeb";
            hday   = WAYEB_DAYS[day_position - 360];
        } else {
            hmonth = HAAB_MONTH[day_position / 20];
            hday   = HAAB_DAYS[day_position % 20];
        }
        return haab_format.format(new Object[]{hday, hmonth});
    }

    public String toGDate() {
        final String wday = WEEK_DAYS[cal.get(Calendar.DAY_OF_WEEK)-1];
        return String.format(Locale.US, "%d-%d-%d(%s)",
               cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE), wday);
    }
    public String toMDate() {
        StringBuilder sb = new StringBuilder();
        return sb.append(this.toLongCount()).append(" : ").append(this.toTzolkin())
                .append("/").append(this.toHaab()).toString();
    }

    public void updateBaseDate(Calendar cal){
        this.cal = cal;
        update(0);
    }

    public void updateBaseDate(int year, int month, int date){
        final GregorianCalendar g = new GregorianCalendar(year, month-1, date);
        g.setGregorianChange(new Date(Long.MIN_VALUE));
        this.cal = g;
        CalUtil.updateCalToNoon(this.cal);
        update(0);
    }

    public void updateBaseDateByLongCount(
            int piktun, int baktun, int katun, int tun, int winal, int kin){
        final long base_days = PIKTUN_BASE * piktun + BAKTUN_BASE * baktun
            + KATUN_BASE * katun + TUN_BASE * tun
            + WINAL_BASE * winal + kin;
        final double j_base = Math.ceil(JULIAN_CONSTANT+0.5);
        final long java_day_base = base_days + this.gmt - new Double(j_base).longValue();
        final Date d = new Date(java_day_base * (24*60*60*1000));
        this.cal.setTime(d);
        CalUtil.updateCalToNoon(this.cal);
        update(0);
    }

    public void updateBaseDateByLongCount(List<Integer> long_count_list) {
        updateBaseDateByLongCount(
                long_count_list.get(0),
                long_count_list.get(1),
                long_count_list.get(2),
                long_count_list.get(3),
                long_count_list.get(4),
                long_count_list.get(5) );
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

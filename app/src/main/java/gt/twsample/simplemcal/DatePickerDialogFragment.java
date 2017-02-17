package gt.twsample.simplemcal;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import gt.twsample.simplemcal.util.CalUtil;
import gt.twsample.simplemcal.util.McalDate;

/**
 * Created by takashi.watanabe on 2017/02/08.
 */

public class DatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    static DatePickerDialogFragment newInstance(McalDate mDate) {
        DatePickerDialogFragment f = new DatePickerDialogFragment();
        Bundle b = new Bundle();
        Calendar cal = mDate.getCal();
        b.putInt("year", cal.get(Calendar.YEAR));
        b.putInt("month", cal.get(Calendar.MONTH));
        b.putInt("day", cal.get(Calendar.DATE));
        f.setArguments(b);
        return f;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        int year  = getArguments().getInt("year");
        int month = getArguments().getInt("month");
        int day   = getArguments().getInt("day");
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
        CalUtil.updateCalToNoon(cal);
        MainActivity ma = (MainActivity)getActivity();
        ma.updateMcal(cal);
        ma.setInputFreeDate(cal);
    }
}

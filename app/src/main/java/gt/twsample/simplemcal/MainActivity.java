package gt.twsample.simplemcal;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import gt.twsample.simplemcal.util.McalDate;

public class MainActivity extends AppCompatActivity {
    private McalDate mcal;
    private TextView g_date;
    private TextView m_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        g_date = (TextView)findViewById(R.id.gregorian_date);
        m_date = (TextView)findViewById(R.id.mcal_date);
        mcal = new McalDate(GregorianCalendar.getInstance());
        g_date.setText(mcal.toGDate());
        final StringBuilder sb = new StringBuilder();
        m_date.setText(sb.append(mcal.toLongCount()).append(" : ").append(mcal.toTzolkin()));

        // Set Calendar Date to Free Date Input
        setInputFreeDate(mcal.getCal());

        // buttons
        Button previous = (Button)findViewById(R.id.previous);
        Button next     = (Button)findViewById(R.id.next);
        Button set_date     = (Button)findViewById(R.id.set_date);
        Button set_free_year = (Button)findViewById(R.id.set_free_year);

        previous.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mcal.previous(1);
                g_date.setText(mcal.toGDate());
                m_date.setText(mcal.toMDate());
            }
        });
        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mcal.next(1);
                g_date.setText(mcal.toGDate());
                m_date.setText(mcal.toMDate());
            }
        });
        set_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // DialogFragment picker = new DatePickerDialogFragment();
                DialogFragment picker = DatePickerDialogFragment.newInstance(mcal);
                picker.show(getSupportFragmentManager(), "date picker");
            }
        });
        set_free_year.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText input_year  = (EditText)findViewById(R.id.input_year);
                EditText input_month = (EditText)findViewById(R.id.input_month);
                EditText input_date  = (EditText)findViewById(R.id.input_date);
                Editable free_year  = input_year.getText();
                Editable free_month = input_month.getText();
                Editable free_date  = input_date.getText();
                mcal.updateBaseDate(
                    Integer.parseInt(free_year.toString()),
                    Integer.parseInt(free_month.toString()),
                    Integer.parseInt(free_date.toString())
                );
                g_date.setText(mcal.toGDate());
                m_date.setText(mcal.toMDate());
            }
        });

        // edit texts
        // EditText input_year = (EditText)findViewById(R.id.input_year);
    }
    public void updateMcal(Calendar cal){
        mcal.updateBaseDate(cal);
        m_date.setText(mcal.toMDate());
        g_date.setText(mcal.toGDate());
    }
    public void setInputFreeDate(Calendar cal){
        final EditText input_year = (EditText)findViewById(R.id.input_year);
        final EditText input_month = (EditText)findViewById(R.id.input_month);
        final EditText input_date = (EditText)findViewById(R.id.input_date);
        input_year.setText(String.valueOf(cal.get(Calendar.YEAR)));
        input_month.setText(String.valueOf((cal.get(Calendar.MONTH)+1)));
        input_date.setText(String.valueOf(cal.get(Calendar.DATE)));
    }
}

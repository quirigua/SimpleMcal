package gt.twsample.simplemcal;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

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
        final GregorianCalendar g = new GregorianCalendar();
        g.setGregorianChange(new Date(Long.MIN_VALUE));
        mcal = new McalDate(g);
        g_date.setText(mcal.toGDate());
        m_date.setText(mcal.toMDate());

        // Set Calendar Date to Free Date Input
        setInputFreeDate(mcal.getCal());
        setInputLongCount(mcal.toLongCountAsArray());

        // buttons
        // 1日戻すボタン
        Button previous = (Button)findViewById(R.id.previous);
        // 1日進めるボタン
        Button next     = (Button)findViewById(R.id.next);
        // カレンダーから日付を設定するボタン
        Button set_date     = (Button)findViewById(R.id.set_date);
        // テキスト入力の年月日を反映するボタン
        Button set_free_year = (Button)findViewById(R.id.set_free_year);
        // テキスト入力の長期暦を反映するボタン
        Button set_long_count = (Button)findViewById(R.id.set_long_count);
        // テキスト入力の長期暦(全)を反映するボタン
        Button set_all_long_count = (Button)findViewById(R.id.set_all_long_count);

        // 1日前ボタン
        // * 表示を1日前にする
        // * 下部のテキストボックスの表示を1日前にする
        previous.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mcal.previous_one();
                setCalendarToAll();
            }
        });
        // 1日後ボタン
        // * 表示を1日後にする
        // * 下部のテキストボックスの表示を1日後にする
        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mcal.next_one();
                setCalendarToAll();
            }
        });
        // カレンダーから設定するボタン
        set_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // DialogFragment picker = new DatePickerDialogFragment();
                DialogFragment picker = DatePickerDialogFragment.newInstance(mcal);
                picker.show(getSupportFragmentManager(), "date picker");
            }
        });
        // テキスト入力の日付を反映するボタン
        set_free_year.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText input_year  = (EditText)findViewById(R.id.input_year);
                EditText input_month = (EditText)findViewById(R.id.input_month);
                EditText input_date  = (EditText)findViewById(R.id.input_date);
                Editable free_year  = input_year.getText();
                Editable free_month = input_month.getText();
                Editable free_date  = input_date.getText();
                int year = Integer.parseInt(free_year.toString());
                if(year< 0){ year++; }
                mcal.updateBaseDate(
                    year,
                    Integer.parseInt(free_month.toString()),
                    Integer.parseInt(free_date.toString())
                );
                g_date.setText(mcal.toGDate());
                m_date.setText(mcal.toMDate());
            }
        });

        set_long_count.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText input_piktun  = (EditText)findViewById(R.id.input_piktun);
                EditText input_baktun  = (EditText)findViewById(R.id.input_baktun);
                EditText input_katun   = (EditText)findViewById(R.id.input_katun);
                EditText input_tun     = (EditText)findViewById(R.id.input_tun);
                EditText input_winal   = (EditText)findViewById(R.id.input_winal);
                EditText input_kin     = (EditText)findViewById(R.id.input_kin);
                final int piktun = Integer.parseInt(input_piktun.getText().toString());
                int baktun = Integer.parseInt(input_baktun.getText().toString());
                if (baktun == 13) { baktun = 0; }
                final int katun  = Integer.parseInt(input_katun.getText().toString());
                final int tun    = Integer.parseInt(input_tun.getText().toString());
                final int winal  = Integer.parseInt(input_winal.getText().toString());
                final int kin    = Integer.parseInt(input_kin.getText().toString());
                mcal.updateBaseDateByLongCount(
                    piktun, baktun, katun, tun, winal, kin);
                g_date.setText(mcal.toGDate());
                m_date.setText(mcal.toMDate());
            }
        });
        set_all_long_count.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final EditText input_long_count = (EditText)findViewById(R.id.input_long_count);
                final String long_count = input_long_count.getText().toString();
                String[] long_count_array = long_count.split(Pattern.quote("."));
                List<Integer> long_count_list = new ArrayList<Integer>();
                // TODO 実装中
                for(int i = 0; i < long_count_array.length; i++) {
                    // piktunの桁が入力されていなかった場合
                    if(i == 0 && long_count_array.length == 5) {
                        if (long_count_array[0] == "13") {
                            long_count_list.add(1);
                        } else {
                            long_count_list.add(0);
                        }
                    }
                    long_count_list.add(Integer.parseInt(long_count_array[i]));
                }

                // カレンダーをアップデート
                mcal.updateBaseDateByLongCount(long_count_list);
                g_date.setText(mcal.toGDate());
                m_date.setText(mcal.toMDate());
            }
        });
    }
    public void setCalendarToAll(){
        g_date.setText(mcal.toGDate());
        m_date.setText(mcal.toMDate());
        setInputFreeDate(mcal.getCal());
    }
    public void updateAllDates(Calendar cal) {
        updateMcal(cal);
        setInputFreeDate(cal);
    }
    public void updateMcal(Calendar cal){
        mcal.updateBaseDate(cal);
        m_date.setText(mcal.toMDate());
        g_date.setText(mcal.toGDate());
    }
    public void setInputFreeDate(Calendar cal){
        final EditText input_year  = (EditText)findViewById(R.id.input_year);
        final EditText input_month = (EditText)findViewById(R.id.input_month);
        final EditText input_date  = (EditText)findViewById(R.id.input_date);
        input_year.setText(String.valueOf(cal.get(Calendar.YEAR)));
        input_month.setText(String.valueOf((cal.get(Calendar.MONTH)+1)));
        input_date.setText(String.valueOf(cal.get(Calendar.DATE)));
    }
    public void setInputLongCount(int[] long_count_array) {
        final EditText input_piktun = (EditText)findViewById(R.id.input_piktun);
        final EditText input_baktun = (EditText)findViewById(R.id.input_baktun);
        final EditText input_katun  = (EditText)findViewById(R.id.input_katun);
        final EditText input_tun    = (EditText)findViewById(R.id.input_tun);
        final EditText input_winal  = (EditText)findViewById(R.id.input_winal);
        final EditText input_kin    = (EditText)findViewById(R.id.input_kin);
        input_piktun.setText(String.valueOf(long_count_array[0]));
        input_baktun.setText(String.valueOf(long_count_array[1]));
        input_katun.setText(String.valueOf(long_count_array[2]));
        input_tun.setText(String.valueOf(long_count_array[3]));
        input_winal.setText(String.valueOf(long_count_array[4]));
        input_kin.setText(String.valueOf(long_count_array[5]));
    }
}

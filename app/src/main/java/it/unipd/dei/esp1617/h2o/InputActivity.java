package it.unipd.dei.esp1617.h2o;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by boemd on 04/04/2017.
 */

public class InputActivity extends AppCompatActivity
{
    private boolean toastNameSent,toastWeightSent;

    int hourS = -1, minS = -1, hourW = -1, minW = -1, hourSp = -1, minSp = -1;
    static final int TIME_DIALOG_ID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        //dati persistenti salvati come SharedpPeferences
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int age = preferences.getInt("age_value",0);
        int weight = preferences.getInt("weight_value",50);
        boolean lessnot = preferences.getBoolean("lessnot_value",false);
        boolean male = preferences.getBoolean("male_value",false);  //male = true, female = false;
        String name = preferences.getString("name_value", "Al Bano Carrisi");
        //mancano gli orari!!

        //aggancio widget con IDs
        final EditText spaceName=(EditText) findViewById(R.id.name_space);
        EditText spaceWeight=(EditText) findViewById(R.id.weight);
        EditText spaceSport=(EditText) findViewById(R.id.sport_time);
        Spinner spinnerSex=(Spinner) findViewById(R.id.sex_spinner);
        final Spinner spinnerAge=(Spinner) findViewById(R.id.age_spinner);
        EditText spaceSleep=(EditText) findViewById(R.id.sleep_time);
        EditText spaceWake=(EditText) findViewById(R.id.wake_time);
        CheckBox checkNot = (CheckBox) findViewById(R.id.less_notifications);

        //CheckBox
        checkNot.setChecked(lessnot);

        //EditText
        if(name==""||name==null)
        {
            name="Al Bano Carrisi";
        }
        spaceName.setText(name);
        spaceWeight.setText(Integer.toString(weight)); //Non è stato messo l'Integer puro perché causava un bug nell'apertura
        //spaceSport.setText();
        //spaceSleep.setText();
        //spaceSleep.setText();

        spaceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!isToastNameSent()&&s.toString().length()>20){
                    Toast.makeText(InputActivity.this, R.string.toast_name, Toast.LENGTH_SHORT).show();
                    setToastNameSent();
                }

            }
        });

        spaceWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!isToastWeightSent()&&Integer.parseInt(s.toString())>199){
                    Toast.makeText(InputActivity.this, R.string.toast_weight,Toast.LENGTH_SHORT).show();
                    setToastWeightSent();
                }

            }
        });

        //Spinner
        ArrayAdapter<CharSequence> sex_adapter = ArrayAdapter.createFromResource(this, R.array.sex_array, android.R.layout.simple_spinner_item);
        sex_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(sex_adapter);
        spinnerSex.setSelection(male?1:0);


        ArrayAdapter<CharSequence> years_adapter = ArrayAdapter.createFromResource(this, R.array.years_array, android.R.layout.simple_spinner_item);
        sex_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAge.setAdapter(years_adapter);
        spinnerAge.setSelection(age);
        //quello che segue è inutile, ma per il momento lo lascio perché non si sa mai
        /*
        spinnerAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerAge.setSelection(spinnerAge.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */

        spaceSleep.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) {
                if (hourS == -1 || minS == -1) {
                    Calendar c = Calendar.getInstance();
                    hourS = c.get(Calendar.HOUR);
                    minS = c.get(Calendar.MINUTE);
                }

                showTimeDialogS(v, hourS, minS);
            }
        });

        spaceWake.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) {
                if (hourW == -1 || minW == -1) {
                    Calendar c = Calendar.getInstance();
                    hourW = c.get(Calendar.HOUR);
                    minW = c.get(Calendar.MINUTE);
                }

                showTimeDialogW(v, hourW, minW);
            }
        });

        spaceSport.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) {
                if (hourSp == -1 || minSp == -1) {
                    Calendar c = Calendar.getInstance();
                    hourSp = c.get(Calendar.HOUR);
                    minSp = c.get(Calendar.MINUTE);
                }

                showTimeDialogSp(v, hourSp, minSp);
            }
        });
    }

    public void showTimeDialogS(View v, int hour, int min) {
        (new TimePickerDialog(InputActivity.this, timeSetListenerS, hour, min, true)).show();
    };

    private TimePickerDialog.OnTimeSetListener timeSetListenerS = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            hourS = hourOfDay;
            minS = minute;
            EditText spaceSleep = (EditText) findViewById(R.id.sleep_time);
            spaceSleep.setText(hourS + " : " + minS);

        }
    };

    public void showTimeDialogW(View v, int hour, int min) {
        (new TimePickerDialog(InputActivity.this, timeSetListenerW, hour, min, true)).show();
    };

    private TimePickerDialog.OnTimeSetListener timeSetListenerW = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            hourW = hourOfDay;
            minW = minute;
            EditText spaceWake = (EditText) findViewById(R.id.wake_time);
            spaceWake.setText(hourW + " : " + minW);

        }
    };

    public void showTimeDialogSp(View v, int hour, int min) {
        (new TimePickerDialog(InputActivity.this, timeSetListenerSp, hour, min, true)).show();
    };

    private TimePickerDialog.OnTimeSetListener timeSetListenerSp = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            hourSp = hourOfDay;
            minSp = minute;
            EditText spaceSleep = (EditText) findViewById(R.id.sport_time);
            spaceSleep.setText(hourSp + " : " + minSp);

        }
    };



    @SuppressLint("CommitPrefsEdit")
    @Override
    protected void onPause()
    {
        super.onPause();

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        //references dei widgets per leggerne lo stato
        Spinner spinnerAge=(Spinner) findViewById(R.id.age_spinner);
        int age = spinnerAge.getSelectedItemPosition();
        EditText spaceWeight=(EditText) findViewById(R.id.weight);
        int weight = Integer.parseInt(spaceWeight.getText().toString());
        CheckBox checkNot = (CheckBox) findViewById(R.id.less_notifications);
        boolean lessnot = checkNot.isChecked();
        Spinner spinnerSex=(Spinner) findViewById(R.id.sex_spinner);
        boolean male = (spinnerSex.getSelectedItemPosition()==1)?true:false;
        EditText spaceName=(EditText) findViewById(R.id.name_space);
        String name = spaceName.getText().toString();

        //salvataggio dello stato persistente
        editor.putInt("age_value",age);
        editor.putInt("weight_value",weight);
        editor.putBoolean("lessnot_value",lessnot);
        editor.putBoolean("male_value",male);
        editor.putString("name_value",name);
        //salvataggio in mutua esclusione
        editor.commit();
    }

    @Override
    protected void onResume(){
        super.onResume();           //i metodi "not-setter" del controllo dei toast vengono chiamati in onResume() così da poter venire chiamati
        setToastNameNotSent();      //ogni volta che l'InputActivity viene riaperta
        setToastWeightNotSent();
    }

    //metodi di controllo del flusso dei Toast
    private boolean isToastNameSent(){
        return toastNameSent;
    }
    private boolean isToastWeightSent(){
        return toastWeightSent;
    }
    private void setToastNameSent(){
        toastNameSent = true;
    }
    private void setToastWeightSent() {
        toastWeightSent = true;
    }
    private void setToastNameNotSent(){
        toastNameSent=false;
    }
    private void setToastWeightNotSent(){
        toastWeightSent=false;
    }

    public int TimeHour(){
        int Hour = hourS - hourW;
        return Hour;
    }

    public int TimeMin(){
        int Min = minS - minW;
        return Min;
    }

    public int TimeFirstHour(){
        return hourW;
    }

    public int TimeFirstMinute(){
        return minW;
    }
    public int SportHour(){
        return hourSp;
    }

    public int SportMin(){
        return minSp;
    }
}

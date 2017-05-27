package it.unipd.dei.esp1617.h2o;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;

/**
 * Created by boemd on 04/04/2017.
 */

public class InputActivity extends AppCompatActivity
{
    private boolean toastNameSent,toastWeightSent;
    private boolean modificationsHaveOccurred = false;
    private NotificationTemplate[] notArray = new NotificationTemplate[24];
    private int hourS = -1, minS = -1, hourW = -1, minW = -1;
    private static final String TAG = "InputActivity";

    /**
     * vengono inizializzati i vari widget e settati i vari listener
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate called");
        setContentView(R.layout.activity_input);

        modificationsHaveOccurred=false;
        //dati persistenti salvati come SharedPeferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int age = preferences.getInt("age_value",0);
        int weight = preferences.getInt("weight_value",50);
        boolean lessnot = preferences.getBoolean("lessnot_value",false);
        boolean sport = preferences.getBoolean("sport_value",false);
        boolean male = preferences.getBoolean("male_value",false);  //male = true, female = false;
        String name = preferences.getString("name_value", "User");
        hourW = preferences.getInt("hour_w", 7);
        minW = preferences.getInt("min_w", 0);
        hourS = preferences.getInt("hour_s", 23);
        minS = preferences.getInt("min_s", 0);

        String wake = preferences.getString("time_wake", (hourW<10?"0":"")+hourW+" : "+(minW<10?"0":"")+minW);
        String sleep = preferences.getString("time_sleep", (hourS<10?"0":"")+hourS+" : "+(minS<10?"0":"")+minS);

        //aggancio widget con IDs
        final EditText spaceName=(EditText) findViewById(R.id.name_space);
        EditText spaceWeight=(EditText) findViewById(R.id.weight);
        Spinner spinnerSex=(Spinner) findViewById(R.id.sex_spinner);
        final Spinner spinnerAge=(Spinner) findViewById(R.id.age_spinner);
        TextView spaceSleep=(TextView) findViewById(R.id.sleep_time);
        TextView spaceWake=(TextView) findViewById(R.id.wake_time);
        CheckBox checkNot = (CheckBox) findViewById(R.id.less_notifications);
        CheckBox checkSport= (CheckBox) findViewById(R.id.sport_box);
        //CheckBox
        checkNot.setChecked(lessnot);
        checkSport.setChecked(sport);

        checkNot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                modificationsHaveOccurred=true;
                Log.d(TAG, "LessNot changed");
            }
        });
        checkSport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                modificationsHaveOccurred=true;
                Log.d(TAG, "Sport changed");
            }
        });

        //EditText
        if(name.equals(""))
        {
            name="User";
        }
        spaceName.setText(name);
        spaceWeight.setText(Integer.toString(weight)); //Non è stato messo l'Integer puro perché causava un bug nell'apertura

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

                modificationsHaveOccurred=true;
                Log.d(TAG, "Weight changed");
                if(!isToastWeightSent()&&Double.parseDouble(0+s.toString())>199){       //parseInt da errore se s è vuota!! E' necessario aggiungere lo 0 iniziale
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

        spaceSleep.setText(sleep);
        spaceWake.setText(wake);

        spaceSleep.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) {
                if (hourS == -1 || minS == -1) {
                    Calendar c = Calendar.getInstance();
                    hourS = c.get(Calendar.HOUR);
                    minS = c.get(Calendar.MINUTE);
                }
                showTimeDialogS(hourS, minS);
            }
        });

        spaceWake.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) {
                if (hourW == -1 || minW == -1) {
                    Calendar c = Calendar.getInstance();
                    hourW = c.get(Calendar.HOUR);
                    minW = c.get(Calendar.MINUTE);
                }

                showTimeDialogW(hourW, minW);
            }
        });

    }

    private void showTimeDialogS(int hour, int min) {
        (new TimePickerDialog(InputActivity.this, timeSetListenerS, hour, min, true)).show();
    }

    private TimePickerDialog.OnTimeSetListener timeSetListenerS = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            hourS = hourOfDay;
            minS = minute;
            TextView spaceSleep = (TextView) findViewById(R.id.sleep_time);
            spaceSleep.setText(new StringBuilder().append((hourS<10?"0":"")).append(hourS).append(" : ").append((minS<10?"0":"")).append(minS));
            modificationsHaveOccurred=true;
            Log.d(TAG, "Sleep changed");
        }
    };

    private void showTimeDialogW(int hour, int min){
        (new TimePickerDialog(InputActivity.this, timeSetListenerW, hour,min,true)).show();
    }

    private TimePickerDialog.OnTimeSetListener timeSetListenerW = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            hourW = hourOfDay;
            minW = minute;
            TextView spaceWake = (TextView) findViewById(R.id.wake_time);
            spaceWake.setText(new StringBuilder().append((hourW<10?"0":"")).append(hourW).append(" : ").append((minW<10?"0":"")).append(minW));
            modificationsHaveOccurred=true;
            Log.d(TAG, "Wake changed");
        }
    };

    /**
     * vengono salvati (o sovrascritti) nelle defaultSharedPreferences i dati di input.
     * Se sono state fatte delle modifiche ai dati, allora la vengono chiamati i metodi di stima della quantità d'acqua da consumare
     * e di conseguenza l'array di NotificationsTemplate viene ricomputato.
     */
    @Override
    protected void onPause(){
        Log.d(TAG,"onPause called");
        super.onPause();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        //references dei widgets per leggerne lo stato
        Spinner spinnerAge=(Spinner) findViewById(R.id.age_spinner);
        int age = spinnerAge.getSelectedItemPosition();
        EditText spaceWeight=(EditText) findViewById(R.id.weight);
        Double w = Double.parseDouble(0+spaceWeight.getText().toString());
        int weight = w.intValue();
        weight = (weight==0)?50:weight;
        Spinner spinnerSex=(Spinner) findViewById(R.id.sex_spinner);
        boolean male = spinnerSex.getSelectedItemPosition()==1;
        EditText spaceName=(EditText) findViewById(R.id.name_space);
        String name = spaceName.getText().toString();
        CheckBox checkNoti = (CheckBox)findViewById(R.id.less_notifications) ;
        boolean lessnot = checkNoti.isChecked();
        CheckBox checkSport = (CheckBox)findViewById(R.id.sport_box) ;
        boolean sport = checkSport.isChecked();
        TextView spaceWake=(TextView) findViewById(R.id.wake_time);
        TextView spaceSleep=(TextView) findViewById(R.id.sleep_time);
        Log.d(TAG,name+" uomo=" +male+" anni="+age+" peso="+weight+" sport="+sport);
        if(preferences.getInt("age_value",0)!=age ||preferences.getBoolean("male_value",false)!=male){
            modificationsHaveOccurred=true;
        }

        //salvataggio dello stato persistente
        editor.putInt("age_value",age);
        editor.putInt("weight_value",weight);
        editor.putBoolean("lessnot_value",lessnot);
        editor.putBoolean("male_value",male);
        editor.putString("name_value",name);
        editor.putBoolean("sport_value",sport);
        editor.putInt("hour_w",hourW);
        editor.putInt("min_w",minW);
        editor.putInt("hour_s",hourS);
        editor.putInt("min_s",minS);
        editor.putString("time_wake", spaceWake.getText().toString());
        editor.putString("time_sleep", spaceSleep.getText().toString());

        if(modificationsHaveOccurred){
            Log.d(TAG, "Modifications have occurred");
            int quantity = getQuantity(age, weight, male, sport);
            editor.putInt("quantity",quantity);
            fillNotArray(quantity, lessnot, hourW, minW,hourS, minS, male,age);

            //INTENT AL SERVICE
            Intent i = new Intent(getApplicationContext(),H2OService.class);
            i.putExtra(H2OService.RESCHEDULE, true);
            Log.d(TAG,"Reschedule intent created in Activity");
            startService(i);
        }
        else{
            Log.d(TAG, "Modifications have NOT occurred");
        }

        //salvataggio in mutua esclusione
        editor.commit();
        Log.d(TAG, "Salvataggio in mutua esclusione");
    }

    /**
     * i metodi "not-setter" del controllo dei toast vengono chiamati in onResume() così da poter venire chiamati
     * ogni volta che l'InputActivity viene riaperta
     */
    @Override
    protected void onResume(){
        super.onResume();
        setToastNameNotSent();
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

    /**
     * algoritmo che determina la quantità d'acqua da consumare giornalmente
     * @param age
     * @param weight
     * @param male
     * @param sport
     * @return quantità d'acqua da consumare giornalmente
     */
    private int getQuantity(int age,int weight,boolean male,boolean sport){
        int quantity; //quantità determinata in ml
        if(age<=2) quantity=600;
        else if(age<5) quantity=800;
        else if(age<10) quantity=1200;
        else if(age<12) quantity=1400;
        else{
            if(male){
                quantity=1600;
                if(age>16)
                    quantity+=200;
                if(sport)
                    quantity+=400;
                if(weight>80)
                    quantity+=200;
            }
            else{
                quantity=1400;
                if(age>16)
                    quantity+=200;
                if(sport)
                    quantity+=400;
                if(weight>70)
                    quantity+=200;
            }
        }
        return quantity;
    }

    /**
     * riempimento delle 24 notifiche giornaliere
     * se non è necessario che la notifica i-esima venga schedulata, allora notArray[i] verrà posto a null
     * @param quantity
     * @param lessnot
     * @param wakeH
     * @param wakeM
     * @param sleepH
     * @param sleepM
     * @param male
     * @param age
     */
    private void fillNotArray(int quantity, boolean lessnot, int wakeH, int wakeM, int sleepH, int sleepM, boolean male,int age){
        int hour = sleepH- wakeH;//se si va a letto dopo mezzanotte, hour diventa negativo. Risoluzione riga successiva
        hour = (hour<0)?(24+hour):(hour);
        Log.d(TAG, "hour="+hour);

        Log.d(TAG, "quantity="+quantity);

        if(lessnot)
        {
            //fisso l'ora delle notifiche
            Calendar c0 = Calendar.getInstance();
            c0.set(Calendar.HOUR_OF_DAY, wakeH+2);
            c0.set(Calendar.MINUTE, 30);

            Calendar c1 = Calendar.getInstance();
            c1.set(Calendar.HOUR_OF_DAY, wakeH+6);
            c1.set(Calendar.MINUTE, 30);

            Calendar c2 = Calendar.getInstance();
            c2.set(Calendar.HOUR_OF_DAY, wakeH+11);
            c2.set(Calendar.MINUTE, 30);

            //fisso la quantità di bicchieri per notifica
            int glasses = (quantity-(quantity%150))/150+1;
            int q;
            switch (glasses%3){
                case 1:  q = (glasses+1)/3;
                    break;
                case 2:  q = (glasses+2)/3;
                    break;
                default: q=(glasses)/3;
                    break;
            }


            notArray[wakeH+2]= new NotificationTemplate(0, c0, q);
            Log.d(TAG,"notifica 0 "+ c0.getTime().getHours()+ ":"+c0.getTime().getMinutes()+" bicchieri ="+q);
            notArray[wakeH+6]= new NotificationTemplate(1, c1, q);
            Log.d(TAG,"notifica 1 "+ c1.getTime().getHours()+ ":"+c1.getTime().getMinutes()+" bicchieri ="+q);
            notArray[wakeH+11]= new NotificationTemplate(2, c2, q);
            Log.d(TAG,"notifica 2 "+ c2.getTime().getHours()+ ":"+c2.getTime().getMinutes()+" bicchieri ="+q);
        }
        else{
            if(wakeH<sleepH){
                for(int i=wakeH; i<sleepH+1; i++){
                    //fisso ora della notifica
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY,i);
                    c.set(Calendar.MINUTE, 30);
                    if(i==wakeH){
                        if (wakeM>50)
                        {
                            c.set(Calendar.HOUR_OF_DAY,i+1);
                            c.set(Calendar.MINUTE, wakeM-50);
                        }
                        else
                            c.set(Calendar.MINUTE, wakeM+10);
                    }
                    if(i==sleepH){
                        if(sleepM<10)
                        {
                            c.set(Calendar.HOUR_OF_DAY,i-1);
                            c.set(Calendar.MINUTE, wakeM+50);
                        }
                        else{
                            c.set(Calendar.MINUTE, sleepM-10);
                        }
                    }
                    //fisso quantità della notifica
                    int q=0;

                    if(age<5){
                        if((i-wakeH)%3==0)
                            q=1;
                    }
                    else if(age<12){
                        if((i-wakeH)%2==0)
                            q=1;
                    }
                    else{
                        if (male) {
                            if(quantity<2100){
                                q=1;
                            }
                            else{
                                if((i-wakeH)%2==0)
                                    q=2;
                                else
                                    q=1;
                            }
                        }
                        else{
                            if(quantity<1900){
                                q=1;
                            }
                            else{
                                if((i-wakeH)%3==0)
                                    q=2;
                                else
                                    q=1;
                            }
                        }
                    }
                    notArray[i]= new NotificationTemplate(i, c, q);
                    Log.d(TAG,"notifica "+i+" "+ c.getTime().getHours()+ ":"+c.getTime().getMinutes()+" bicchieri ="+q);
                }
            }
            else{
                for(int i=wakeH; i<24;i++){
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY,i);
                    c.set(Calendar.MINUTE, 30);
                    if(i==wakeH){
                        if (wakeM>50)
                        {
                            c.set(Calendar.HOUR_OF_DAY,i+1);
                            c.set(Calendar.MINUTE, wakeM-50);
                        }
                        else
                            c.set(Calendar.MINUTE, wakeM+10);
                    }
                    if(i==sleepH){
                        if(sleepM<10)
                        {
                            c.set(Calendar.HOUR_OF_DAY,i-1);
                            c.set(Calendar.MINUTE, wakeM+50);
                        }
                        else{
                            c.set(Calendar.MINUTE, sleepM-10);
                        }
                    }

                    //fisso quantità della notifica
                    int q=0;

                    if(age<5){
                        if((i-wakeH)%3==0)
                            q=1;
                    }
                    else if(age<12){
                        if((i-wakeH)%2==0)
                            q=1;
                    }
                    else{
                        if (male) {
                            if(quantity<2100){
                                q=1;
                            }
                            else{
                                if((i-wakeH)%2==0)
                                    q=2;
                                else
                                    q=1;
                            }
                        }
                        else{
                            if(quantity<1900){
                                q=1;
                            }
                            else{
                                if((i-wakeH)%3==0)
                                    q=2;
                                else
                                    q=1;
                            }
                        }
                    }

                    notArray[i]= new NotificationTemplate(i, c, q);
                    Log.d(TAG,"notifica "+i+" "+ c.getTime().getHours()+ ":"+c.getTime().getMinutes()+" bicchieri ="+q);
                }
                for(int i=0; i< sleepH+1; i++){
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY,i);
                    c.set(Calendar.MINUTE, 30);
                    if(i==wakeH){
                        if (wakeM>50)
                        {
                            c.set(Calendar.HOUR_OF_DAY,i+1);
                            c.set(Calendar.MINUTE, wakeM-50);
                        }
                        else
                            c.set(Calendar.MINUTE, wakeM+10);
                    }
                    if(i==sleepH){
                        if(sleepM<10)
                        {
                            c.set(Calendar.HOUR_OF_DAY,i-1);
                            c.set(Calendar.MINUTE, wakeM+50);
                        }
                        else{
                            c.set(Calendar.MINUTE, sleepM-10);
                        }
                    }

                    //fisso quantità della notifica
                    int q=0;

                    if(age<5){
                        if((i-wakeH)%3==0)
                            q=1;
                    }
                    else if(age<12){
                        if((i-wakeH)%2==0)
                            q=1;
                    }
                    else{
                        if (male) {
                            if(quantity<2100){
                                q=1;
                            }
                            else{
                                if((i-wakeH)%2==0)
                                    q=2;
                                else
                                    q=1;
                            }
                        }
                        else{
                            if(quantity<1900){
                                q=1;
                            }
                            else{
                                if((i-wakeH)%3==0)
                                    q=2;
                                else
                                    q=1;
                            }
                        }
                    }
                    notArray[i]= new NotificationTemplate(i, c, q);
                    Log.d(TAG,"notifica "+i+" "+ c.getTime().getHours()+ ":"+c.getTime().getMinutes()+" bicchieri ="+q);
                }
            }

        }
        storeNotArray();
    }


    /**
     * sovrascrivo il file con l'informazione contenuta nell'array notArray appena riempito
     * questo metodo viene chiamato all'interno del metodo fillNotArray()
     */
    private void storeNotArray(){
        try{
            FileOutputStream fos = (InputActivity.this).openFileOutput("notificationsTemplateContainer.obj", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for(int i=0; i<24; i++){
                oos.writeObject(notArray[i]);
            }
            oos.close();
            fos.close();
            Log.d(TAG, "Storage NotificationTemplate eseguito");
        }
        catch(FileNotFoundException e){
            Log.d(TAG, getResources().getString(R.string.file_not_found));
        }
        catch (IOException e){
            Log.d(TAG, getResources().getString(R.string.io_exception));
        }
    }
}
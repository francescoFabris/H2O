package it.unipd.dei.esp1617.h2o;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.io.FileReader;
import java.util.Calendar;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button bu;
    private TextView tv1,tv2,tv3,tv4;
    private FloatingActionButton faplus, faminus;
    private static int drunkGlasses;
    InputActivity Hour = new InputActivity();
    InputActivity Min = new InputActivity();
    InputActivity FirstHour = new InputActivity();
    InputActivity FirstMin = new InputActivity();
    private PendingIntent pendingIntent;
    private AlarmManager mAlarmManager;

    int hour = Hour.TimeHour();
    int min = Min.TimeMin();
    int HourW = FirstHour.TimeFirstHour();
    int MinW = FirstMin.TimeFirstMinute();
    Calendar firingCal= Calendar.getInstance();
    Calendar currentCal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        drunkGlasses=preferences.getInt("drunk_glasses",0);

        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);
        tv4 = (TextView) findViewById(R.id.textView4);
        changeViewsText();

        bu = (Button) findViewById(R.id.apri_second);
        bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start InputActivity
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent);
            }
        });

        faplus = (FloatingActionButton)findViewById(R.id.plus_button);
        faminus = (FloatingActionButton)findViewById(R.id.minus_button);
        //Incremento del numero di bicchieri bevuti dall'utente
        faplus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                incrementGlasses();
            }
        });
        //Decremento del numero di bicchieri bevuti dall'utente
        faminus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                decrementGlasses();
            }
        });
        /*
        Intent myIntent = new Intent(MainActivity.this, MyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent,0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        */
        firingCal.setTimeInMillis(System.currentTimeMillis());
        firingCal.set(Calendar.HOUR, HourW);
        firingCal.set(Calendar.MINUTE, MinW);
        firingCal.set(Calendar.SECOND, 0);

        long intendedTime = firingCal.getTimeInMillis();
        long currentTime = currentCal.getTimeInMillis();

        mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, MyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        mAlarmManager.setRepeating(AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_HOUR, pendingIntent);
    }

    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("drunk_glasses",drunkGlasses);
        editor.apply();
    }

    public void incrementGlasses(){
        drunkGlasses++;
        changeViewsText();
    }
    public void decrementGlasses(){
        if(drunkGlasses>0){
            drunkGlasses--;
            changeViewsText();
        }

    }
    public void changeViewsText(){
        tv2.setText(Integer.toString(drunkGlasses));
        tv4.setText((drunkGlasses>5)?R.string.c2:R.string.c1);
    }

    public void GlassPerHour (){
        int GlassPerHour = drunkGlasses/hour;// calcola i bicchieri all'ora da bere

    }

}


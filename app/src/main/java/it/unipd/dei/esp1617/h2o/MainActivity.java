package it.unipd.dei.esp1617.h2o;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private Button bu;
    private TextView tv1,tv2,tv3,tv4;
    private FloatingActionButton faplus, faminus;
    private static int drunkGlasses;
    private boolean toastNegSent;
    private ImageView imageMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        drunkGlasses = preferences.getInt("drunk_glasses", 0);


        tv2 = (TextView) findViewById(R.id.textView2);
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

        faplus = (FloatingActionButton) findViewById(R.id.plus_button);
        faminus = (FloatingActionButton) findViewById(R.id.minus_button);
        //Incremento del numero di bicchieri bevuti dall'utente
        faplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementGlasses();
            }
        });
        //Decremento del numero di bicchieri bevuti dall'utente
        faminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementGlasses();
            }
        });

        imageMan = (ImageView) findViewById(R.id.donut);

    }


    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("drunk_glasses",drunkGlasses);
        editor.apply();
    }

    @Override
    protected void onResume(){
        super.onResume();
        setToastNegNotSent();
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
        else if(!isToastNegSent()){
            Toast.makeText(MainActivity.this, R.string.toast_neg,Toast.LENGTH_SHORT).show();
            setToastNegSent();
        }


    }
    public void changeViewsText(){
        int drunkMl = drunkGlasses*200;
        String toShow = Integer.toString(drunkMl) + " ml";
        tv2.setText(toShow);
        tv4.setText((drunkGlasses>5)?R.string.c2:R.string.c1);
        if(0 == drunkGlasses) {
            imageMan.setImageResource(R.drawable.man1);
        }
        if(1 == drunkGlasses) {
            imageMan.setImageResource(R.drawable.man2);
        }
        if(2 == drunkGlasses) {
            imageMan.setImageResource(R.drawable.man3);
        }
        if(3 == drunkGlasses) {
            imageMan.setImageResource(R.drawable.man4);
        }
 //       if(4 == drunkGlasses) {
  //          imageMan.setImageResource(R.drawable.man5);  // BUG (non so perchè)
   //     }
        if(5 == drunkGlasses) {
            imageMan.setImageResource(R.drawable.man6);
        }
        if(6 == drunkGlasses) {
            imageMan.setImageResource(R.drawable.man7);
        }
        if(7 == drunkGlasses) {
            imageMan.setImageResource(R.drawable.man8);
        }
        if(8 == drunkGlasses) {
            imageMan.setImageResource(R.drawable.man9);
        }
        if(9 == drunkGlasses) {
            imageMan.setImageResource(R.drawable.man10);
        }
        if(10 == drunkGlasses) {
            imageMan.setImageResource(R.drawable.man11);
        }

    }


    private boolean isToastNegSent(){
        return toastNegSent;
    }
    private void setToastNegSent(){
        toastNegSent=true;
    }
    private void setToastNegNotSent(){
        toastNegSent=false;
    }
}

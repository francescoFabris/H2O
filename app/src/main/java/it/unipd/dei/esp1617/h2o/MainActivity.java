package it.unipd.dei.esp1617.h2o;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView tv2,tv4;
    private static int drunkGlasses;
    private static int quantity;
    private static int totalGlasses;
    private boolean toastNegSent;
    private boolean male;
    private ImageView imageMan;
    private int ShowcaseDelay = 1000;
    private static final String SHOWCASE_ID = "01";
    private FloatingActionButton faplus;
    private FloatingActionButton faminus;
    private Button option;
    private Button setZero;
    private ImageView iGlass;

    /**
     * vengono inizializzati i vari widget e settati i vari listener
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate called");
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        drunkGlasses=preferences.getInt("drunk_glasses",0);
        quantity=preferences.getInt("quantity",0);
        Log.d(TAG,"Quantity taken="+quantity);
        totalGlasses = quantity/150;
        male=preferences.getBoolean("male_value",false);
        Log.d(TAG, "Sex="+ (male?"male":"female"));
        int gl = getGlasses(getIntent());
        if(gl!=0){
            drunkGlasses += gl;
        }
        //tv1 = (TextView) findViewById(R.id.textView1);inutile
        tv2 = (TextView) findViewById(R.id.textView2);
        //tv3 = (TextView) findViewById(R.id.textView3);inutile
        tv4 = (TextView) findViewById(R.id.textView4);
        changeViewsText();

        option = (Button) findViewById(R.id.apri_second);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start InputActivity
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent);
            }
        });

        setZero = (Button) findViewById(R.id.zero_button);
        setZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drunkGlasses=0;
                changeViewsText();
                Log.d(TAG,"Set zero : OK");
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

        iGlass=(ImageView)findViewById(R.id.imageViewGlass) ;
        iGlass.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                NotificationTemplate nt = new NotificationTemplate(25,null,3);
                NotificationHandler nHan = new NotificationHandler(getApplicationContext(), nt);
                nHan.displayReply();
                Log.d(TAG, "notificata notifica DI PROVA");
                return true;
            }
        });

        presentShowcaseSequence();
    }

    /**
     * è necessario solamente salvare nelle DefaultSharedPreferences il numero di bicchieri bevuti
     */
    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG,"onPause called");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("drunk_glasses",drunkGlasses);
        editor.commit();
    }

    /**
     * vengono ripristinati dalle DefaultSharedPreferences il numero di bicchieri bevuti
     * e ad essi vengono sommati quelli aggiunti tramite notifica
     * in fine la grafica viene aggiornata in base ai nuovi dati
     */
    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG,"onResume called");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        drunkGlasses=preferences.getInt("drunk_glasses",0);
        quantity=preferences.getInt("quantity",0);
        Log.d(TAG,"Quantity taken="+quantity);
        totalGlasses = quantity/200;
        male=preferences.getBoolean("male_value",false);
        Log.d(TAG, "Sex="+(male?"male":"female"));
        int gl = getGlasses(getIntent());
        if(gl!=0){
            drunkGlasses += gl;
        }
        changeViewsText();
        setToastNegNotSent();
    }

    private void incrementGlasses(){
        drunkGlasses++;
        changeViewsText();
        Log.d(TAG,"drunkGlasses="+drunkGlasses);
        Log.d(TAG,"totalGlasses="+totalGlasses);
    }
    private void decrementGlasses(){
        if(drunkGlasses>0){
            drunkGlasses--;
            changeViewsText();
            Log.d(TAG,"drunkGlasses="+drunkGlasses);
            Log.d(TAG,"totalGlasses="+totalGlasses);
        }
        else if(!isToastNegSent()){
            Toast.makeText(MainActivity.this, R.string.toast_neg,Toast.LENGTH_SHORT).show();
            setToastNegSent();
        }


    }

    /**
     * aggiornamento della grafica
     */
    private void changeViewsText(){
        Log.d(TAG,"changeViewsText called");
        int drunkMl = drunkGlasses*200;
        String toShow = Integer.toString(drunkMl) + " ml / "+quantity+" ml";
        tv2.setText(toShow);
        tv4.setText((drunkGlasses>3*quantity/5)?R.string.c2:R.string.c1);
        changeImageDonut();

    }

    /**
     * modifica dell'ImageDonut
     */
    private void changeImageDonut(){
        Log.d(TAG,"changeImageDonut called");
        imageMan = (ImageView) findViewById(R.id.donut);
        //FAI UN IF CHE CONTEMPLI L'IPOTESI DI totalGlasses = 0
        //ALL'INIZIO E'COSI


        if (male) {
            if(0 == drunkGlasses) {
                imageMan.setImageResource(R.drawable.man1);
                Log.d(TAG, "Set ImageMan1");
            }
            else if(1 == drunkGlasses) {
                imageMan.setImageResource(R.drawable.man2);
                Log.d(TAG, "Set ImageMan2");
            }
            else if(Math.ceil(2*totalGlasses/12) == drunkGlasses) {
                imageMan.setImageResource(R.drawable.man3);
                Log.d(TAG, "Set ImageMan3");
            }
            else if(Math.ceil(3*totalGlasses/12) == drunkGlasses) {
                imageMan.setImageResource(R.drawable.man4);
                Log.d(TAG, "Set ImageMan4");
            }
            else if(Math.ceil(4*totalGlasses/12) == drunkGlasses) {
                imageMan.setImageResource(R.drawable.man5);
                Log.d(TAG, "Set ImageMan5");
            }
            else if(Math.ceil(5*totalGlasses/12) == drunkGlasses) {
                imageMan.setImageResource(R.drawable.man6);
                Log.d(TAG, "Set ImageMan6");
            }
            else if(Math.ceil(6*totalGlasses/12) == drunkGlasses) {
                imageMan.setImageResource(R.drawable.man7);
                Log.d(TAG, "Set ImageMan7");
            }
            else if(Math.ceil(7*totalGlasses/12) == drunkGlasses) {
                imageMan.setImageResource(R.drawable.man8);
                Log.d(TAG, "Set ImageMan8");
            }
            else if(Math.ceil(8*totalGlasses/12) == drunkGlasses) {
                imageMan.setImageResource(R.drawable.man9);
                Log.d(TAG, "Set ImageMan9");
            }
            else if(Math.ceil(9*totalGlasses/12) == drunkGlasses) {
                imageMan.setImageResource(R.drawable.man10);
                Log.d(TAG, "Set ImageMan10");
            }
            else if(Math.ceil(10*totalGlasses/12) == drunkGlasses) {
                imageMan.setImageResource(R.drawable.man11);
                Log.d(TAG, "Set ImageMan11");
            }
            else if(totalGlasses <= drunkGlasses) {
                imageMan.setImageResource(R.drawable.man12);
                Log.d(TAG, "Set ImageMan12");
            }
        }
        else
        {
            if(0 == drunkGlasses) {
                imageMan.setImageResource(R.drawable.woman1);
                Log.d(TAG, "Set ImageWoman1");
            }
            else if(1 == drunkGlasses) {
                imageMan.setImageResource(R.drawable.woman2);
                Log.d(TAG, "Set ImageWoman2");
            }
            else if((Math.ceil(2*totalGlasses/12) == drunkGlasses)) {
                imageMan.setImageResource(R.drawable.woman3);
                Log.d(TAG, "Set ImageWoman3");
            }
            else if((Math.ceil(3*totalGlasses/12) == drunkGlasses)) {
                imageMan.setImageResource(R.drawable.woman4);
                Log.d(TAG, "Set ImageWoman4");
            }
            else if((Math.ceil(4*totalGlasses/12) == drunkGlasses)) {
                imageMan.setImageResource(R.drawable.woman5);
                Log.d(TAG, "Set ImageWoman5");
            }
            else if((Math.ceil(5*totalGlasses/12) == drunkGlasses)) {
                imageMan.setImageResource(R.drawable.woman6);
                Log.d(TAG, "Set ImageWoman6");
            }
            else if((Math.ceil(6*totalGlasses/12) == drunkGlasses)) {
                imageMan.setImageResource(R.drawable.woman7);
                Log.d(TAG, "Set ImageWoman7");
            }
            else if((Math.ceil(7*totalGlasses/12) == drunkGlasses)) {
                imageMan.setImageResource(R.drawable.woman8);
                Log.d(TAG, "Set ImageWoman8");
            }
            else if((Math.ceil(8*totalGlasses/12) == drunkGlasses)) {
                imageMan.setImageResource(R.drawable.woman9);
                Log.d(TAG, "Set ImageWoman9");
            }
            else if((Math.ceil(9*totalGlasses/12) == drunkGlasses)) {
                imageMan.setImageResource(R.drawable.woman10);
                Log.d(TAG, "Set ImageWoman10");
            }
            else if((Math.ceil(10*totalGlasses/12) == drunkGlasses)) {
                imageMan.setImageResource(R.drawable.woman11);
                Log.d(TAG, "Set ImageWoman11");
            }
            else if(totalGlasses <= drunkGlasses) {
                imageMan.setImageResource(R.drawable.woman12);
                Log.d(TAG, "Set ImageWoman12");
            }
        }

    }

    /**
     * viene estrapolato dall'extra dell'intent il numero di bicchieri
     * segnalato dall'utente dalla notifica
     * @param intent
     * @return numero di bicchieri segnalato dall'utente
     */
    private int getGlasses(Intent intent){
        Log.d(TAG,"getGlasses called");
        Integer ret = 0;
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        try{
            if (remoteInput != null) {
                int id = intent.getIntExtra("ID", 25);
                NotificationHandler nh = new NotificationHandler(getApplicationContext(), null);
                nh.dismissDirectReplayNotification(id);
                ret = Integer.parseInt(remoteInput.getCharSequence(NotificationHandler.KEY_TEXT_REPLY).toString());
                if(ret==null)ret=0;
                ret = (ret>0)?ret:0;
            }
        }
        catch (Exception e){
            ret=0;
        }
        return ret;
    }


    //metodi di controllo della ripetizione dei toast
    private boolean isToastNegSent(){
        return toastNegSent;
    }
    private void setToastNegSent(){
        toastNegSent=true;
    }
    private void setToastNegNotSent(){
        toastNegSent=false;
    }

    private void presentShowcaseSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {

            }
        });

        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(faplus)
                        .setDismissText("GOT IT")
                        .setContentText("ADD 1 GLASS (200ml)")
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(faminus)
                        .setDismissText("GOT IT")
                        .setContentText("REMOVE 1 GLASS (200ml)")
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(setZero)
                        .setDismissText("GOT IT")
                        .setContentText("SET DRUNK GLASSES = 0")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(option)
                        .setDismissText("GOT IT")
                        .setContentText("BUT FIRST INSERT YOUR DATA")
                        .withRectangleShape()
                        .build()
        );



        sequence.start();

    }

}
package it.unipd.dei.esp1617.h2o;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by boemd on 19/05/2017.
 */

    class NotificationHandler {
    private NotificationTemplate nt;
    private NotificationManager nMan;
    private int drunkGlasses;
    private boolean lessNot;
    private Context context;
    SharedPreferences preferences;
    public static final String KEY_TEXT_REPLY = "key_text_reply";
    private String name;
    private static final String TAG = "NotificationHAndler";

    /**
     * inizializzazione variabili da parametri passati in input e da DefaultSharedPreferences
     * @param context
     * @param nt
     */
    public NotificationHandler(Context context, NotificationTemplate nt){
        this.context=context;
        this.nt = nt;
        nMan = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        drunkGlasses = preferences.getInt("drunk_glasses", 0);
        lessNot = preferences.getBoolean("lessnot_value",false);
        name = preferences.getString("name_value", "User");
    }

    public void displayReply(){
        Log.d(TAG, "displayReply() called");
        //intent che viene inviato a MainActivity
        Intent in = new Intent(context, MainActivity.class);
        in.putExtra("ID",nt.getId());

        PendingIntent directReplayIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), in, 0);

        //azione di risposta con RemoteInput(oggetto che specifica l'input e invia intent)
        NotificationCompat.Action directReplayAction =
                new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Reply", directReplayIntent)
                        .addRemoteInput(new RemoteInput.Builder(KEY_TEXT_REPLY)
                                .setLabel("How many glasses have you drunk?")
                                .build())
                        .setAllowGeneratedReplies(true)
                        .build();
        Log.d(TAG, "Action created");

        //costruzione stringa da notificare
        String str;
        if(!lessNot){
            str="1 glass";
            if(nt.getNumberOfGlasses()==2){
                str="2 glasses";
            }
            if(nt.getNumberOfGlasses()==3){
                str="3 glasses";
            }
        }
        else{
            str = nt.getNumberOfGlasses() +"glasses";
        }

        //assemblaggio notifica
        Notification noti = new NotificationCompat.Builder(context)
                .setContentText("Hey "+name+", drink "+str+" of water!")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .addAction(directReplayAction)
                .setVibrate(new long[] { 1000, 500,1000 })  //permission richiesta nel Manifest
                .setAutoCancel(true)
                .build();
        Log.d(TAG, "Notification assembled");
        //notifica all'utente
        nMan.notify(nt.getId(), noti);
        Log.d(TAG, "Notified " +nt.getId());
    }

    public void dismissDirectReplayNotification(int id) {
        nMan.cancel(id);
    }

}

package it.unipd.dei.esp1617.h2o;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Calendar;

public class H2OReceiver extends BroadcastReceiver {

    private NotificationTemplate[] notArray = new NotificationTemplate[24];
    private static final String TAG = "H2OReceiver";
    public static final String NOTIFICATION = "Notification";
    private Context context;

    /**
     * in base al contenuto dell'intent ricevuto, viene deciso se:
     *      -ri-schedulare tutte le notifiche
     *      -mostrare un notifica tramite l'azione di un NotificationHandler
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {


        this.context = context;
        if (("android.intent.action.DATE_CHANGED").equals(intent.getAction())||
                ("android.intent.action.BOOT_COMPLETED").equals(intent.getAction())) {
            Log.d(TAG,"arrivato intent DATE o BOOT");
            Intent i = new Intent(context, H2OService.class);
            i.putExtra(H2OService.RESCHEDULE, true);
            Log.d(TAG,"Reschedule intent created in Receiver");
            context.startService(i);
            Log.d(TAG, "startService() called");
        } else if (intent.getBooleanExtra(NOTIFICATION, false)) {
            Log.d(TAG,"Arrived intent NOTIFICATION");
            getNotArray();
            int id = intent.getIntExtra(H2OService.ID, 24);
            NotificationTemplate nt = notArray[id];
            Calendar c = Calendar.getInstance();
            if(c.get(Calendar.HOUR_OF_DAY)==nt.getWhen().get(Calendar.HOUR_OF_DAY)){          //risoluzione bug notifiche multiple
                NotificationHandler nHan = new NotificationHandler(context,nt);
                nHan.displayReply();
                Log.d(TAG, "notificata notifica "+id);
            }

        }
    }

    /**
     * notArray viene riempito in base a ciò che viene letto dal file "notificationsTemplateContainer.obj"
     */
    private void getNotArray(){
        try{
            FileInputStream fis = context.getApplicationContext().openFileInput("notificationsTemplateContainer.obj");
            ObjectInputStream ois = new ObjectInputStream(fis);
            for(int i=0;i<24;i++){
                notArray[i]=(NotificationTemplate) ois.readObject();
            }
            ois.close();
            fis.close();
            Log.d(TAG, "Array recovery : OK");
        }
        catch(FileNotFoundException e){
            Log.d(TAG, context.getApplicationContext().getApplicationContext().getResources().getString(R.string.file_not_found));
        }
        catch (ClassNotFoundException e){
            Log.d(TAG, context.getApplicationContext().getResources().getString(R.string.io_exception));
        }
        catch (IOException e){
            Log.d(TAG, context.getApplicationContext().getResources().getString(R.string.class_not_found));
        }
    }
}
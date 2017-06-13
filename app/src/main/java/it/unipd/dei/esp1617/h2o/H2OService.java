package it.unipd.dei.esp1617.h2o;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Calendar;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class H2OService extends Service{

    public static final String RESCHEDULE = "Reschedule_Notifications";
    public static final String ID = "id";
    private static final String TAG = "H2OService";
    private NotificationTemplate[] notArray = new NotificationTemplate[24];


    private AlarmManager alMan;

    public H2OService() {
    }

    public void onCreate(){
        alMan = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG,"Service created");
    }

    /**
     * se è necessario schedulare le 24 notifiche, il service chiama il metodo scheduleNotifications();
     * @param intent
     * @param flags
     * @param startId
     * @return Service.START_STICKY se il Service viene terminato, lo si deve immediatamente ricreare
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG,"onStartCommand called");
        if(intent==null || intent.getBooleanExtra(RESCHEDULE, false)){
            scheduleNotifications();
            Log.d(TAG,"Notifications scheduled");
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * notArray viene riempito in base a ciò che viene letto dal file "notificationsTemplateContainer.obj"
     */
    private void getNotArray(){
        try{
            FileInputStream fis = this.openFileInput("notificationsTemplateContainer.obj");
            ObjectInputStream ois = new ObjectInputStream(fis);
            for(int i=0;i<24;i++){
                notArray[i]=(NotificationTemplate) ois.readObject();
            }
            ois.close();
            fis.close();
        }
        catch(FileNotFoundException e){
            Log.d(TAG, getResources().getString(R.string.file_not_found));
        }
        catch (ClassNotFoundException e){
            Log.d(TAG, getResources().getString(R.string.io_exception));
        }
        catch (IOException e){
            Log.d(TAG, getResources().getString(R.string.class_not_found));
        }
    }

    /**
     * settati al più 24 alarms contenenti un PendingIntent contenente a sua volta le informazioni necessarie al Broadcast Receiver
     * per mostrare la notifica
     * si vuole poi che la stessa notifica venga ripetuta ogni giorno
     *
     * Il request code di ogni PendingIntent è l'ora in cui la notifica deve venire mostrat
     * così in caso di rescheduling verranno considerati solamente i nuovi PendingIntent
     * evitando così inconvenevoli sovrapposizioni
     */
    private void scheduleNotifications(){
        Log.d(TAG, "scheduleNotifications called");
        getNotArray();
        for(int i=0; i<24; i++){
            if(notArray[i]!=null&&notArray[i].getNumberOfGlasses()>0){
                Intent intent = new Intent(this, H2OReceiver.class);
                intent.putExtra(ID, notArray[i].getId());
                intent.putExtra(H2OReceiver.NOTIFICATION,true);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notArray[i].getId(),intent,FLAG_UPDATE_CURRENT); //attenzione!!!!!!
                long when=notArray[i].getWhen().getTime().getTime();
                if(when<Calendar.getInstance().getTime().getTime()){
                    when+=24*60*60*1000;
                }
                alMan.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
                alMan.setRepeating(AlarmManager.RTC_WAKEUP, when,24*60*60*1000, pendingIntent);   // ripetizione giornaliera
                Log.d(TAG,"Scheduled alarm #"+i+" in "+ ((when-Calendar.getInstance().getTime().getTime())/1000)+ " seconds");
            }
        }
    }
}
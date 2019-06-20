package com.meilinger.tj.cssvalkyrieemployee_v2.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.meilinger.tj.cssvalkyrieemployee_v2.alarm.receiver.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AlarmCreator extends AsyncTask<Void, Void, Void> {


    private Context context;
    private Date startTime, endTime;


    public AlarmCreator(Context context, Date startTime, Date endTime) {
        this.context = context;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static void cancelAlarms(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        if (PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null) {
            alarmManager.cancel(PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
        if (PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_NO_CREATE) != null) {
            alarmManager.cancel(PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
        if (PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_NO_CREATE) != null) {
            alarmManager.cancel(PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context, AlarmReceiver.class);


        long diff = endTime.getTime() - startTime.getTime();
        diff = TimeUnit.MILLISECONDS.toHours(diff);

        Calendar c = Calendar.getInstance();
        c.setTime(startTime);

        if(diff <= 8 && diff >= 5){
            c.add(Calendar.HOUR_OF_DAY, 4);
            System.out.println("Alarm set for " + new SimpleDateFormat("M/d/yy h:mm.s").format(c.getTime()));
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        } else if(diff <= 10){
            c.add(Calendar.HOUR_OF_DAY, 4);
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), PendingIntent.getBroadcast(context, 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            System.out.println("Alarm set for " + new SimpleDateFormat("M/d/yy h:mm.s").format(c.getTime()));
            c.add(Calendar.HOUR_OF_DAY, 2);
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), PendingIntent.getBroadcast(context, 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            System.out.println("Alarm set for " + new SimpleDateFormat("M/d/yy h:mm.s").format(c.getTime()));
        } else {
            c.add(Calendar.HOUR_OF_DAY, 4);
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), PendingIntent.getBroadcast(context, 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            System.out.println("Alarm set for " + new SimpleDateFormat("M/d/yy h:mm.s").format(c.getTime()));
            c.add(Calendar.HOUR_OF_DAY, 4);
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), PendingIntent.getBroadcast(context, 2, myIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            System.out.println("Alarm set for " + new SimpleDateFormat("M/d/yy h:mm.s").format(c.getTime()));
        }
        return null;
    }

}

package com.meilinger.tj.cssvalkyrieemployee_v2.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.meilinger.tj.cssvalkyrieemployee_v2.R;


/**
 * Created by TJ on 4/3/2018.
 */

public class NotificationCreator {
    public final static int SCHEDULE_UPDATED = 17;
    public final static int BREAK = 11;

    private String title = "Valkyrie", content = "Valkyrie";
    private Context context;
    private int notificationID = -1;
    private boolean hasSound = false;
    private NotificationCompat.Builder mBuilder;
    private Intent intent;

    public NotificationCreator(Context context, int notificationID, Intent intent) {
        this.context = context;
        this.notificationID = notificationID;
        this.intent = intent;
        initialize();
    }

    private void initialize(){
        PendingIntent pendingIntent = null;

        if(intent != null){
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        }

        if(notificationID == SCHEDULE_UPDATED){
            content = "The schedule has been updated.";
        } else if(notificationID == BREAK){
            content = "Break";
            hasSound = true;
        }
        long[] vib = {500, 500, 500};
        mBuilder = new NotificationCompat.Builder(context, NotificationCompat.CATEGORY_MESSAGE)
                .setSmallIcon(R.drawable.icon_valkyrie_employee)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(vib);
        if(hasSound){
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        }
    }

    public void show(){
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(notificationID, mBuilder.build());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }
}

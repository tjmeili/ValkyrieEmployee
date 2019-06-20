package com.meilinger.tj.cssvalkyrieemployee_v2.alarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.meilinger.tj.cssvalkyrieemployee_v2.notification.NotificationCreator;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new NotificationCreator(context, NotificationCreator.BREAK, null).show();
    }
}

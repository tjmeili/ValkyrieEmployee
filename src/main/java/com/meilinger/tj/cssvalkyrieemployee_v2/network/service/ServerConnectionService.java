package com.meilinger.tj.cssvalkyrieemployee_v2.network.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.meilinger.tj.cssvalkyrieemployee_v2.SharedPreferencesHandler;
import com.meilinger.tj.cssvalkyrieemployee_v2.activity.MainActivity;
import com.meilinger.tj.cssvalkyrieemployee_v2.local.data.DataHolder;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.data.NetworkDataHolder;
import com.meilinger.tj.cssvalkyrieemployee_v2.notification.NotificationCreator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import server.data.Day;

public class ServerConnectionService extends Service {
    private static final String TAG = ServerConnectionService.class.getName();

    public static final int MSG_REGISTER    = 1;
    public static final int MSG_UNREGISTER  = 0;
    private static final String EXTRA_SCHEDULE = "extra_schedule";

    public static final int MSG_SCHEDULE_UPDATED = 301;
    public static final int MSG_SERVER_INFO_UPDATED = 901;

    private Messenger clientMessenger = null;

    private ServerConnectionRunnable serverConnectionRunnable = null;
    private static boolean serviceRunning = false;

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(serverConnectionRunnable == null){
            serverConnectionRunnable = new ServerConnectionRunnable();
            new Thread(serverConnectionRunnable).start();
            if(!serviceRunning){
                serviceRunning = true;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serverConnectionRunnable.endServer();
        if(serviceRunning){
            serviceRunning = false;
        }
    }



    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_REGISTER:
                    clientMessenger = msg.replyTo;
                    break;
                case MSG_UNREGISTER:
                    clientMessenger = null;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void notifyScheduleUpdated(ArrayList<Day> schedule){
        if(schedule != null){
            if (clientMessenger != null) {
                Message msg = Message.obtain(null, MSG_SCHEDULE_UPDATED, schedule);
                try {
                    clientMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRA_SCHEDULE, schedule);
            new NotificationCreator(this, NotificationCreator.SCHEDULE_UPDATED, intent).show();
        }
    }

    private void notifyServerInfoUpdated(String serverIp, int serverPort){
        SharedPreferencesHandler prefsHandler = new SharedPreferencesHandler(getApplicationContext());
        prefsHandler.saveServerIp(serverIp);
        prefsHandler.saveServerPort(serverPort);
        Log.i(TAG, "Server network information updated.");

        if(clientMessenger != null){
            Message msg = Message.obtain(null, MSG_SERVER_INFO_UPDATED);
            Bundle data = new Bundle();
            data.putString("serverIP", serverIp);
            data.putInt("serverPort", serverPort);
            msg.setData(data);
            try {
                clientMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private class ServerConnectionRunnable implements Runnable{

        private Socket socket = null;
        private ServerSocket serverSocket = null;
        private static final int NOTIFY_SCHEDULE_UPDATED = 301;
        private static final int NOTIFY_SERVER_INFO = 901;
        private static final int REQUEST_EMPLOYEE_INFO = 444;

        private boolean deviceServerRunning = false;

        @Override
        public void run() {
            startServer();
            while(deviceServerRunning){
                Socket receivingSocket = null;
                try {
                    Log.d(TAG, "run: Starting connection...");
                    receivingSocket = serverSocket.accept();
                    Log.d(TAG, "run: Connected.");
                    ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(receivingSocket.getOutputStream()));
                    output.flush();
                    ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(receivingSocket.getInputStream()));

                    int request = input.readInt();
                    switch (request){
                        case NOTIFY_SCHEDULE_UPDATED:
                            ArrayList<Day> schedule = null;
                            try {
                                schedule = (ArrayList<Day>) input.readObject();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            notifyScheduleUpdated(schedule);
                            break;
                        case NOTIFY_SERVER_INFO:
                            String serverIP = input.readUTF();
                            int serverPort = input.readInt();
                            notifyServerInfoUpdated(serverIP, serverPort);
                            break;
                        case REQUEST_EMPLOYEE_INFO:
                            output.writeObject(DataHolder.getInstance().getTimesheet());
                            output.flush();
                            break;
                    }
                    input.close();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(receivingSocket != null){
                        try {
                            receivingSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            endServer();
        }
        private void startServer(){
            try {
                serverSocket = new ServerSocket(NetworkDataHolder.getDevicePort());
                deviceServerRunning = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void endServer(){
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

package com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task;

import android.os.AsyncTask;
import android.util.Log;

import com.meilinger.tj.cssvalkyrieemployee_v2.network.data.NetworkDataHolder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import server.data.Day;

public class RequestScheduleAsyncTask extends AsyncTask<Void, Void, ArrayList<Day>>{
    private static final String TAG = RequestScheduleAsyncTask.class.getPackage().getName();

    private Socket socket = null;

    private static final int REQUEST_SCHEDULE           = 107;

    public interface RequestScheduleAsyncTaskListener{
        void onScheduleReceived(ArrayList<Day> schedule);
    }

    private RequestScheduleAsyncTaskListener requestScheduleAsyncTaskListener = null;

    @Override
    protected ArrayList<Day> doInBackground(Void... voids) {
        ArrayList<Day> schedule = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(NetworkDataHolder.getServerIP(), NetworkDataHolder.getServerPort()), 10000);
            Log.d(TAG, "doInBackground: Attempting to get schedule from server...");
            ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            output.flush();
            ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            output.writeInt(REQUEST_SCHEDULE);
            output.flush();
            try{
                schedule = (ArrayList<Day>) input.readObject();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
            Log.d(TAG, "doInBackground: Schedule received.");
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: Error connecting to server.", e);
        } finally {
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return schedule;
    }

    public RequestScheduleAsyncTaskListener getRequestScheduleAsyncTaskListener() {
        return requestScheduleAsyncTaskListener;
    }

    public void setRequestScheduleAsyncTaskListener(RequestScheduleAsyncTaskListener requestScheduleAsyncTaskListener) {
        this.requestScheduleAsyncTaskListener = requestScheduleAsyncTaskListener;
    }

    @Override
    protected void onPostExecute(ArrayList<Day> schedule) {
        super.onPostExecute(schedule);
        if(requestScheduleAsyncTaskListener != null){
            requestScheduleAsyncTaskListener.onScheduleReceived(schedule);
        }
    }
}

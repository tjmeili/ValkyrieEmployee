package com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task;

import android.os.AsyncTask;

import com.meilinger.tj.cssvalkyrieemployee_v2.network.data.NetworkDataHolder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

import server.data.EmployeeTimesheet;

public class RequestTimesheetAsyncTask extends AsyncTask<Void, Void, EmployeeTimesheet> {
    private static final String TAG = RequestScheduleAsyncTask.class.getName();

    private static final int REQUEST_TIMESHEET          = 108;
    private RequestTimesheetAsyncTaskListener requestTimesheetAsyncTaskListener = null;
    private Socket socket = null;
    private UUID uuid = null;

    public RequestTimesheetAsyncTask(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    protected EmployeeTimesheet doInBackground(Void... voids) {
        EmployeeTimesheet timesheet = null;

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(NetworkDataHolder.getServerIP(), NetworkDataHolder.getServerPort()), 10000);
            ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            output.flush();
            ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            output.writeInt(REQUEST_TIMESHEET);
            output.flush();
            output.writeObject(uuid);
            output.flush();
            try {
                timesheet = (EmployeeTimesheet) input.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return timesheet;
    }

    @Override
    protected void onPostExecute(EmployeeTimesheet timesheet) {
        super.onPostExecute(timesheet);
        if(requestTimesheetAsyncTaskListener != null){
            requestTimesheetAsyncTaskListener.onTimesheetRecieved(timesheet);
        }
    }

    public RequestTimesheetAsyncTaskListener getRequestTimesheetAsyncTaskListener() {
        return requestTimesheetAsyncTaskListener;
    }

    public void setRequestTimesheetAsyncTaskListener(RequestTimesheetAsyncTaskListener requestTimesheetAsyncTaskListener) {
        this.requestTimesheetAsyncTaskListener = requestTimesheetAsyncTaskListener;
    }

    public interface RequestTimesheetAsyncTaskListener{
        void onTimesheetRecieved(EmployeeTimesheet timesheet);
    }
}

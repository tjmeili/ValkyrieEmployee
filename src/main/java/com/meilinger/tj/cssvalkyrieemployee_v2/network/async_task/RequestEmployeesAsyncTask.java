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
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import server.data.Employee;

public class RequestEmployeesAsyncTask extends AsyncTask<Void, Void, ArrayList<Employee>> {
    private static final String TAG = RequestEmployeesAsyncTask.class.getPackage().getName();
    private static final int REQUEST_EMPLOYEES          = 109;

    private Socket socket = null;

    public interface RequestEmployeesAsyncTaskListener{
        void onEmployeesReceived(ArrayList<Employee> employees);
    }

    private RequestEmployeesAsyncTaskListener requestEmployeesAsyncTaskListener = null;

    @Override
    protected ArrayList<Employee> doInBackground(Void... voids) {
        ArrayList<Employee> employees = null;
        try{
            socket = new Socket();
            socket.connect(new InetSocketAddress(NetworkDataHolder.getServerIP(), NetworkDataHolder.getServerPort()), 10000);
            Log.d(TAG, "doInBackground: Opening Streams...");
            ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            output.flush();
            ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            output.writeInt(REQUEST_EMPLOYEES);
            output.flush();
            employees = (ArrayList<Employee>) input.readObject();

            input.close();
            output.close();
        } catch(SocketTimeoutException e){
            Log.e(TAG, "doInBackground: socket timeout", e);
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
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
        return employees;
    }

    public RequestEmployeesAsyncTaskListener getRequestEmployeesAsyncTaskListener() {
        return requestEmployeesAsyncTaskListener;
    }

    public void setRequestEmployeesAsyncTaskListener(RequestEmployeesAsyncTaskListener requestEmployeesAsyncTaskListener) {
        this.requestEmployeesAsyncTaskListener = requestEmployeesAsyncTaskListener;
    }

    @Override
    protected void onPostExecute(ArrayList<Employee> employees) {
        super.onPostExecute(employees);
        if(requestEmployeesAsyncTaskListener != null){
            requestEmployeesAsyncTaskListener.onEmployeesReceived(employees);
        }
    }
}

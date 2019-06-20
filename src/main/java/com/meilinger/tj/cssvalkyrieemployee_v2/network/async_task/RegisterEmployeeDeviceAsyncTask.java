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
import java.util.UUID;

public class RegisterEmployeeDeviceAsyncTask extends AsyncTask<Void, Void, AsyncTaskResultListener.Result>{
    private static final String TAG = RegisterEmployeeDeviceAsyncTask.class.getPackage().getName();
    private UUID uuid = null;
    private Socket socket = null;

    private static final int REGISTER_EMPLOYEE_DEVICE   = 103;

    private AsyncTaskResultListener asyncTaskResultListener = null;

    public RegisterEmployeeDeviceAsyncTask(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    protected AsyncTaskResultListener.Result doInBackground(Void... voids) {
        AsyncTaskResultListener.Result result;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(NetworkDataHolder.getServerIP(), NetworkDataHolder.getServerPort()), 10000);
            Log.d(TAG, "doInBackground: Registering employee device with server...");
            ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            output.flush();
            ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            output.writeInt(REGISTER_EMPLOYEE_DEVICE);
            output.flush();
            output.writeObject(uuid);
            output.flush();
            output.writeInt(NetworkDataHolder.getDevicePort());
            output.flush();

            output.close();
            input.close();
            result = AsyncTaskResultListener.Result.SUCCESS;
            Log.d(TAG, "doInBackground: Device registered.");
        } catch (IOException e) {
            result = AsyncTaskResultListener.Result.FAILED;
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
        return result;
    }

    @Override
    protected void onPostExecute(AsyncTaskResultListener.Result result) {
        super.onPostExecute(result);
        if(asyncTaskResultListener != null){
            asyncTaskResultListener.onProcessFinished(result);
        }
    }

    public AsyncTaskResultListener getAsyncTaskResultListener() {
        return asyncTaskResultListener;
    }

    public void setAsyncTaskResultListener(AsyncTaskResultListener asyncTaskResultListener) {
        this.asyncTaskResultListener = asyncTaskResultListener;
    }
}

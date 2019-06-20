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
import java.util.Date;
import java.util.UUID;

public class SendPunchOutAsyncTask extends AsyncTask<Void, Void, AsyncTaskResultListener.Result> {
    private static final String TAG = SendPunchOutAsyncTask.class.getName();
    private static final int PUNCH_OUT                  = 105;
    private Date punchOutTime = null;
    private UUID uuid = null;
    private Socket socket = null;
    private AsyncTaskResultListener asyncTaskResultListener = null;


    public SendPunchOutAsyncTask(UUID uuid, Date punchOutTime) {
        this.uuid = uuid;
        this.punchOutTime = punchOutTime;
    }


    @Override
    protected AsyncTaskResultListener.Result doInBackground(Void... voids) {
        AsyncTaskResultListener.Result result;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(NetworkDataHolder.getServerIP(), NetworkDataHolder.getServerPort()), 10000);
            Log.d(TAG, "doInBackground: Sending punch out time...");
            ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            output.flush();
            ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            output.writeInt(PUNCH_OUT);
            output.flush();
            output.writeObject(uuid);
            output.flush();
            output.writeObject(punchOutTime);
            output.flush();

            output.close();
            input.close();
            result = AsyncTaskResultListener.Result.SUCCESS;
            Log.d(TAG, "doInBackground: Punch out time sent.");
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
        if (asyncTaskResultListener != null){
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

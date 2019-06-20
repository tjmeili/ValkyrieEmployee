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

public class SendPunchInAsyncTask extends AsyncTask<Void, Void, AsyncTaskResultListener.Result>{
    private static final String TAG = SendPunchInAsyncTask.class.getName();
    private static final int PUNCH_IN                   = 104;

    private Socket socket = null;
    private Date punchInTime = null;
    private UUID uuid = null;
    private AsyncTaskResultListener asyncTaskResultListener = null;

    public SendPunchInAsyncTask(UUID uuid, Date punchInTime) {
        this.uuid = uuid;
        this.punchInTime = punchInTime;
    }

    @Override
    protected AsyncTaskResultListener.Result doInBackground(Void... voids) {
        AsyncTaskResultListener.Result result;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(NetworkDataHolder.getServerIP(), NetworkDataHolder.getServerPort()), 10000);
            Log.d(TAG, "doInBackground: Sending punch in time...");
            ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            output.flush();
            ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            output.writeInt(PUNCH_IN);
            output.flush();
            output.writeObject(uuid);
            output.flush();
            output.writeObject(punchInTime);
            output.flush();

            output.close();
            input.close();
            result = AsyncTaskResultListener.Result.SUCCESS;
            Log.d(TAG, "doInBackground: Punch in time sent.");
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

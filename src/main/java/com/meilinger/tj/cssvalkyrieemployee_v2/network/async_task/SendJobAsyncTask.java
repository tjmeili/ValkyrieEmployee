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

import server.data.Job;

public class SendJobAsyncTask extends AsyncTask<Void, Void, AsyncTaskResultListener.Result>{
    private static final String TAG = SendJobAsyncTask.class.getName();
    private Job job = null;
    private UUID uuid = null;
    private Socket socket = null;

    private static final int NEW_JOB                    = 106;

    private AsyncTaskResultListener asyncTaskResultListener = null;

    public SendJobAsyncTask(UUID uuid, Job job) {
        this.uuid = uuid;
        this.job = job;
    }

    @Override
    protected AsyncTaskResultListener.Result doInBackground(Void... voids) {
        AsyncTaskResultListener.Result result;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(NetworkDataHolder.getServerIP(), NetworkDataHolder.getServerPort()), 10000);
            Log.d(TAG, "doInBackground: Sending job...");
            ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            output.flush();
            ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            output.writeInt(NEW_JOB);
            output.flush();
            output.writeObject(uuid);
            output.flush();
            output.writeInt(job.getJobNumber());
            output.flush();
            output.writeLong(job.getStartTime().getTime());

            output.close();
            input.close();
            result = AsyncTaskResultListener.Result.SUCCESS;
            Log.d(TAG, "doInBackground: Job sent.");
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

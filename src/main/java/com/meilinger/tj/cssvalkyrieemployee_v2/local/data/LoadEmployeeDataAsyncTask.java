package com.meilinger.tj.cssvalkyrieemployee_v2.local.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import server.data.EmployeeTimesheet;

public class LoadEmployeeDataAsyncTask extends AsyncTask<Void, Void, EmployeeTimesheet> {
    private static final String TAG = LoadEmployeeDataAsyncTask.class.getName();

    private String fileName = "data_timesheet.txt";
    private FileInputStream fis = null;

    public interface LoadEmployeeDataAsyncTaskListener{
        void onEmployeeDataLoaded(EmployeeTimesheet timesheet);
    }

    private LoadEmployeeDataAsyncTaskListener loadEmployeeDataAsyncTaskListener = null;

    public LoadEmployeeDataAsyncTask(FileInputStream fis, String fileName) {
        this.fileName = fileName;
        this.fis = fis;
    }

    @Override
    protected EmployeeTimesheet doInBackground(Void... voids) {
        EmployeeTimesheet timesheet = null;
        File dataFile = new File(fileName);
        if(dataFile.exists()){
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(fis);
                try {
                    timesheet = (EmployeeTimesheet) ois.readObject();
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "loadTimesheet: Error reading file.", e);
                }
                fis.close();
                ois.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){
                Log.e(TAG, "loadTimesheet: Error loading timesheet.", e);
            } finally {
                if(fis != null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (ois != null){
                    try {
                        ois.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return timesheet;
    }

    @Override
    protected void onPostExecute(EmployeeTimesheet timesheet) {
        super.onPostExecute(timesheet);
        if(loadEmployeeDataAsyncTaskListener != null){
            loadEmployeeDataAsyncTaskListener.onEmployeeDataLoaded(timesheet);
        }
    }

    public LoadEmployeeDataAsyncTaskListener getLoadEmployeeDataAsyncTaskListener() {
        return loadEmployeeDataAsyncTaskListener;
    }

    public void setLoadEmployeeDataAsyncTaskListener(LoadEmployeeDataAsyncTaskListener loadEmployeeDataAsyncTaskListener) {
        this.loadEmployeeDataAsyncTaskListener = loadEmployeeDataAsyncTaskListener;
    }
}

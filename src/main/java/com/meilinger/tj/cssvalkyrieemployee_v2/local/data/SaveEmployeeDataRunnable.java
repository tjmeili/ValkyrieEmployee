package com.meilinger.tj.cssvalkyrieemployee_v2.local.data;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import server.data.EmployeeTimesheet;

public class SaveEmployeeDataRunnable implements  Runnable{
    private static final String TAG = SaveEmployeeDataRunnable.class.getName();

    private FileOutputStream fos = null;

    private EmployeeTimesheet timesheet = null;

    public SaveEmployeeDataRunnable(FileOutputStream fos, EmployeeTimesheet timesheet) {
        this.fos = fos;
        this.timesheet = timesheet;
    }

    @Override
    public void run() {
        ObjectOutputStream oos = null;
        try {
            Log.d(TAG, "saveTimesheet: Saving timesheet...");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(timesheet);
            Log.d(TAG, "saveTimesheet: Timesheet saved.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "saveTimesheet: Error opening file.", e);
        } catch (IOException e){
            Log.e(TAG, "saveTimesheet: Error saving timesheet.", e);
            e.printStackTrace();
        } finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package com.meilinger.tj.cssvalkyrieemployee_v2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.meilinger.tj.cssvalkyrieemployee_v2.network.data.NetworkDataHolder;

import java.util.UUID;

import server.data.EmployeeTimesheet;

public class SharedPreferencesHandler {
    private static final String TAG = SharedPreferencesHandler.class.getName();

    private Context context;
    private SharedPreferences prefs;

    private static final String KEY_UUID                = "KEY_UUID";
    private static final String KEY_FIRST_NAME          = "KEY_FIRST_NAME";
    private static final String KEY_LAST_NAME           = "KEY_LAST_NAME";
    private static final String KEY_PREV_STARTED        = "KEY_PREV_STARTED";
    private static final String KEY_SERVER_IP           = "KEY_SERVER_IP";
    private static final String KEY_SERVER_PORT         = "KEY_SERVER_PORT";
    private static final String KEY_EMPLOYEE_SELECTED   = "KEY_EMPLOYEE_SELECTED";

    public SharedPreferencesHandler(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getEmployeeSelected(){
        return prefs.getBoolean(KEY_EMPLOYEE_SELECTED, false);
    }

    public void setEmployeeSelected(Boolean employeeSelected){
        prefs.edit().putBoolean(KEY_EMPLOYEE_SELECTED, employeeSelected).apply();
    }

    public boolean getPreviouslyStarted(){
        return prefs.getBoolean(KEY_PREV_STARTED, false);
    }

    public void setPreviouslyStarted(Boolean previouslyStarted){
        Log.i(TAG, "setPreviouslyStarted: " + previouslyStarted);
        prefs.edit().putBoolean(KEY_PREV_STARTED, previouslyStarted).apply();
    }

    public void saveEmployeeInfo(EmployeeTimesheet timesheet){
        Log.i(TAG, "saveEmployeeInfo ");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_UUID, timesheet.getEmployeeID().toString());
        editor.putString(KEY_FIRST_NAME, timesheet.getFirstName());
        editor.putString(KEY_LAST_NAME, timesheet.getLastName());
        editor.apply();
    }

    public UUID getEmployeeID(){
        String sID = prefs.getString(KEY_UUID, "");
        if(!sID.isEmpty()){
            return UUID.fromString(sID);
        }
        return null;
    }

    public String getEmployeeFirstName(){
        return prefs.getString(KEY_FIRST_NAME, "");
    }

    public String getEmployeeLastName(){
        return prefs.getString(KEY_LAST_NAME, "");
    }

    public String getServerIP(){
        return prefs.getString(KEY_SERVER_IP, "");
    }

    public void saveServerIp(String ip){
        Log.i(TAG, "saveServerIp: " + ip);
        prefs.edit().putString(KEY_SERVER_IP, ip).apply();
    }

    public void saveServerPort(int serverPort){
        Log.i(TAG, "saveServerPort: " + serverPort);
        prefs.edit().putInt(KEY_SERVER_PORT, serverPort).apply();
    }

    public int getServerPort(){
        return prefs.getInt(KEY_SERVER_PORT, NetworkDataHolder.getServerPort());
    }

}

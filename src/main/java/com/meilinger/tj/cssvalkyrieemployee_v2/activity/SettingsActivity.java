package com.meilinger.tj.cssvalkyrieemployee_v2.activity;

import android.net.wifi.WifiManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meilinger.tj.cssvalkyrieemployee_v2.R;
import com.meilinger.tj.cssvalkyrieemployee_v2.SharedPreferencesHandler;
import com.meilinger.tj.cssvalkyrieemployee_v2.local.data.DataHolder;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task.AsyncTaskResultListener;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task.TestConnectionAsyncTask;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.data.NetworkDataHolder;

import java.text.SimpleDateFormat;

import server.data.EmployeeTimesheet;



public class SettingsActivity extends AppCompatActivity{
    private static final String TAG = SettingsActivity.class.getName();
    private ConstraintLayout clConnectionStatus;
    private ProgressBar pbConnectionStatus;
    private TextView tvConnectionStatus;
    private ImageView ivConnectionStatus;
    private EditText etServerIP;
    private Button btnApply;

    private EmployeeTimesheet timesheet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        timesheet = DataHolder.getInstance().getTimesheet();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm.ss a");
        ((TextView) findViewById(R.id.tvName)).setText(timesheet.getFirstName() + " " + timesheet.getLastName());
        if(timesheet.getPunchInTime() != null){
            ((TextView) findViewById(R.id.tvClockInTime)).setText(formatter.format(timesheet.getPunchInTime()));
        }
        if(timesheet.getPunchOutTime() != null){
            ((TextView) findViewById(R.id.tvClockOutTime)).setText(formatter.format(timesheet.getPunchOutTime()));
        }
        if(timesheet.getJobs().size() > 0){
            ((TextView) findViewById(R.id.tvCurrentJobNumber)).setText(timesheet.getJobs().get(timesheet.getJobs().size() - 1).getJobNumber());
        }
        @SuppressWarnings("deprecation")
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if(wifiManager.getConnectionInfo() != null){
            ((TextView) findViewById(R.id.tvDeviceIP)).setText(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        }

        btnApply = (Button) findViewById(R.id.btnApply);
        btnApply.setEnabled(false);
        etServerIP = (EditText) findViewById(R.id.etServerIP);
        etServerIP.setText(NetworkDataHolder.getServerIP());
        etServerIP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnApply.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clConnectionStatus = (ConstraintLayout) findViewById(R.id.clConnectionStatus);
        pbConnectionStatus = (ProgressBar) findViewById(R.id.pbConnectionStatus);
        tvConnectionStatus = (TextView) findViewById(R.id.tvConnectionStatus);
        ivConnectionStatus = (ImageView) findViewById(R.id.ivConnectionStatus);
        clConnectionStatus.setVisibility(View.INVISIBLE);
        btnApply = (Button) findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newServerIP = etServerIP.getText().toString().replace(" ", "");
                Log.d(TAG, "onClick: " + newServerIP);
                NetworkDataHolder.setServerIP(newServerIP);
                SharedPreferencesHandler sharedPreferencesHandler = new SharedPreferencesHandler(SettingsActivity.this);
                sharedPreferencesHandler.saveServerIp(newServerIP);
                testConnection();
                btnApply.setEnabled(false);
            }
        });

        ((Button)findViewById(R.id.testConnectionButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testConnection();
            }
        });

    }

    private void testConnection(){
        setViewsConnecting();
        TestConnectionAsyncTask testConnectionAsyncTask = new TestConnectionAsyncTask();
        testConnectionAsyncTask.setAsyncTaskResultListener(new AsyncTaskResultListener() {
            @Override
            public void onProcessFinished(Result result) {
                if(result == Result.SUCCESS){
                    setViewsConnectionSuccess();
                } else {
                    setViewsConnectionFailure();
                }
            }
        });
        testConnectionAsyncTask.execute();
    }

    private void setViewsConnecting(){
        clConnectionStatus.setVisibility(View.VISIBLE);
        tvConnectionStatus.setText("Connecting...");
        ivConnectionStatus.setVisibility(View.GONE);
        pbConnectionStatus.setVisibility(View.VISIBLE);
    }

    private void setViewsConnectionSuccess(){
        tvConnectionStatus.setText("Connected successfully.");
        ivConnectionStatus.setVisibility(View.VISIBLE);
        pbConnectionStatus.setVisibility(View.GONE);
        ivConnectionStatus.setImageResource(R.drawable.check);
    }

    private void setViewsConnectionFailure(){
        tvConnectionStatus.setText("Failed to connect to server.");
        ivConnectionStatus.setVisibility(View.VISIBLE);
        pbConnectionStatus.setVisibility(View.GONE);
        ivConnectionStatus.setImageResource(R.drawable.cross);
    }

}

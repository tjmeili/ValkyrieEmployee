package com.meilinger.tj.cssvalkyrieemployee_v2.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task.AsyncTaskResultListener;
import com.meilinger.tj.cssvalkyrieemployee_v2.R;
import com.meilinger.tj.cssvalkyrieemployee_v2.SharedPreferencesHandler;
import com.meilinger.tj.cssvalkyrieemployee_v2.adapter.ViewPagerAdapter;
import com.meilinger.tj.cssvalkyrieemployee_v2.alarm.AlarmCreator;
import com.meilinger.tj.cssvalkyrieemployee_v2.fragment.HomeFragment;
import com.meilinger.tj.cssvalkyrieemployee_v2.fragment.dialog.EnterJobDialogFragment;
import com.meilinger.tj.cssvalkyrieemployee_v2.fragment.dialog.SafeWorkDayDialogFragment;
import com.meilinger.tj.cssvalkyrieemployee_v2.fragment.dialog.SelectEmployeeDialogFragment;
import com.meilinger.tj.cssvalkyrieemployee_v2.local.data.DataHolder;
import com.meilinger.tj.cssvalkyrieemployee_v2.local.data.LoadEmployeeDataAsyncTask;
import com.meilinger.tj.cssvalkyrieemployee_v2.local.data.SaveEmployeeDataRunnable;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task.RegisterEmployeeDeviceAsyncTask;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task.RequestEmployeesAsyncTask;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task.RequestScheduleAsyncTask;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task.RequestTimesheetAsyncTask;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task.SendJobAsyncTask;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task.SendPunchInAsyncTask;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task.SendPunchOutAsyncTask;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task.SendUnsafeWorkdayAsyncTask;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.data.NetworkDataHolder;
import com.meilinger.tj.cssvalkyrieemployee_v2.network.service.ServerConnectionService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import server.data.Day;
import server.data.Employee;
import server.data.EmployeeTimesheet;
import server.data.Job;


public class MainActivity extends FragmentActivity implements HomeFragment.OnHomeInteractionListener,
        RequestEmployeesAsyncTask.RequestEmployeesAsyncTaskListener,
        RequestScheduleAsyncTask.RequestScheduleAsyncTaskListener,
        EnterJobDialogFragment.JobDialogListener,
        SelectEmployeeDialogFragment.EmployeeSelectListener,
        RequestTimesheetAsyncTask.RequestTimesheetAsyncTaskListener,
        SafeWorkDayDialogFragment.SafeWorkDayDialogListener {

    private static final String TAG = MainActivity.class.getName();
    private static final String FILE_NAME = "data_timesheet.txt";
    private static final String EXTRA_SCHEDULE = "extra_schedule";

    private ConstraintLayout loadingPanel;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private MenuItem prevMenuItem;

    private boolean mBound = false;
    private Messenger mService = null;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    // Connection to service for sending and receiving data from server
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            mBound = true;

            Message msg = Message.obtain(null, ServerConnectionService.MSG_REGISTER);
            msg.replyTo = mMessenger;

            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message msg = Message.obtain(null, ServerConnectionService.MSG_UNREGISTER);
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mService = null;
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingPanel = (ConstraintLayout) findViewById(R.id.loadingPanel);

        checkFirstStart();
        initialize();
        if(getIntent().getExtras() != null){
            ArrayList<Day> schedule = (ArrayList<Day>) getIntent().getSerializableExtra(EXTRA_SCHEDULE);
            if(schedule != null){
                viewPager.setCurrentItem(0);
                DataHolder.getInstance().setSchedule(schedule);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startServerConnectionService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopServerConnectionService();
        if(DataHolder.getInstance().getTimesheet().getPunchOutTime() == null){
            saveEmployeeData(DataHolder.getInstance().getTimesheet());
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void initialize(){
        initializeViewPager();
        initializeNavigationView();
        viewPager.setCurrentItem(1);
    }

    private void initializeNavigationView(){
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_schedule:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_timesheet:
                        viewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        };
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void initializeViewPager(){
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*if(position == 0){
                    viewPagerAdapter.refreshSchedule(schedule);
                } else {
                    viewPagerAdapter.refreshTimesheet(timesheet);
                }*/

                if(prevMenuItem != null){
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(1).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void startServerConnectionService(){
        Log.d(TAG, "START SERVER SERVICE");
        Intent intent = new Intent(MainActivity.this, ServerConnectionService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_IMPORTANT);
    }

    private void stopServerConnectionService(){
        stopService(new Intent(this, ServerConnectionService.class));
    }

    private void checkFirstStart(){
        SharedPreferencesHandler preferencesHandler = new SharedPreferencesHandler(this);
        if(!preferencesHandler.getPreviouslyStarted()){
            requestEmployeesFromServer();
            preferencesHandler.saveServerIp(NetworkDataHolder.getServerIP());
            preferencesHandler.saveServerPort(NetworkDataHolder.getServerPort());
            preferencesHandler.setPreviouslyStarted(true);
        } else if(!preferencesHandler.getEmployeeSelected()){
            requestEmployeesFromServer();
            NetworkDataHolder.setServerIP(preferencesHandler.getServerIP());
            NetworkDataHolder.setServerPort(preferencesHandler.getServerPort());
        } else {
            loadingPanel.setVisibility(View.GONE);
            NetworkDataHolder.setServerIP(preferencesHandler.getServerIP());
            NetworkDataHolder.setServerPort(preferencesHandler.getServerPort());
            UUID uuid = preferencesHandler.getEmployeeID();
            String firstName = preferencesHandler.getEmployeeFirstName();
            String lastName = preferencesHandler.getEmployeeLastName();
            DataHolder.getInstance().setTimesheetInfo(firstName, lastName, uuid);
            requestTimesheetFromServer(uuid);
        }
        requestScheduleFromServer();
    }

    private void saveEmployeeData(EmployeeTimesheet timesheet){
        try {
            FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            new Thread(new SaveEmployeeDataRunnable(fileOutputStream, timesheet)).start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadEmployeeData(){
        System.out.println("Loading employee Timesheet");
        try {
            FileInputStream fis = getApplicationContext().openFileInput(FILE_NAME);
            LoadEmployeeDataAsyncTask loadEmployeeDataAsyncTask = new LoadEmployeeDataAsyncTask(fis, FILE_NAME);
            loadEmployeeDataAsyncTask.setLoadEmployeeDataAsyncTaskListener(new LoadEmployeeDataAsyncTask.LoadEmployeeDataAsyncTaskListener() {
                @Override
                public void onEmployeeDataLoaded(EmployeeTimesheet timesheet) {
                    if(timesheet != null){
                        DataHolder.getInstance().setTimesheet(timesheet);
                    }
                }
            });
            loadEmployeeDataAsyncTask.execute();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "loadEmployeeData: Employee data file does not exist.");
        }
    }

   /* private void sendPunchInTime(Date punchInTime){
        if(!mBound) return;
        Message msg = Message.obtain(null, ServerConnectionService.MSG_PUNCH_IN, tempUUID);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServerConnectionService.MSG_DATA_PUNCH_IN, punchInTime);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }*/

    private void requestTimesheetFromServer(UUID uuid){
        RequestTimesheetAsyncTask requestTimesheetAsyncTask = new RequestTimesheetAsyncTask(uuid);
        requestTimesheetAsyncTask.setRequestTimesheetAsyncTaskListener(this);
        requestTimesheetAsyncTask.execute();
    }

    private void requestScheduleFromServer(){
        RequestScheduleAsyncTask requestScheduleAsyncTask = new RequestScheduleAsyncTask();
        requestScheduleAsyncTask.setRequestScheduleAsyncTaskListener(this);
        requestScheduleAsyncTask.execute();
    }

    private void requestEmployeesFromServer(){
        RequestEmployeesAsyncTask requestEmployeesAsyncTask = new RequestEmployeesAsyncTask();
        requestEmployeesAsyncTask.setRequestEmployeesAsyncTaskListener(this);
        requestEmployeesAsyncTask.execute();
    }

    private void sendJobToServer(Job job){
        SendJobAsyncTask sendJobAsyncTask = new SendJobAsyncTask(DataHolder.getInstance().getUuid(), job);
        sendJobAsyncTask.setAsyncTaskResultListener(new AsyncTaskResultListener() {
            @Override
            public void onProcessFinished(Result result) {
                if(result == Result.FAILED){
                    Toast.makeText(MainActivity.this, "Could not connect to server.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sendJobAsyncTask.execute();
    }

    private void sendPunchInToServer(Date punchInTime){
        SendPunchInAsyncTask sendPunchInAsyncTask = new SendPunchInAsyncTask(DataHolder.getInstance().getUuid(), punchInTime);
        sendPunchInAsyncTask.setAsyncTaskResultListener(new AsyncTaskResultListener() {
            @Override
            public void onProcessFinished(Result result) {
                if(result == Result.FAILED){
                    Toast.makeText(MainActivity.this, "Could not connect to server.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sendPunchInAsyncTask.execute();
    }

    private void sendPunchOutToServer(Date punchOutTime){
        SendPunchOutAsyncTask sendPunchOutAsyncTask = new SendPunchOutAsyncTask(DataHolder.getInstance().getUuid(), punchOutTime);
        sendPunchOutAsyncTask.setAsyncTaskResultListener(new AsyncTaskResultListener() {
            @Override
            public void onProcessFinished(Result result) {
                if(result == Result.FAILED){
                    Toast.makeText(MainActivity.this, "Could not connect to server.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sendPunchOutAsyncTask.execute();
    }

    private void registerDeviceWithServer(){
        RegisterEmployeeDeviceAsyncTask registerEmployeeDeviceAsyncTask = new RegisterEmployeeDeviceAsyncTask(DataHolder.getInstance().getUuid());
        registerEmployeeDeviceAsyncTask.setAsyncTaskResultListener(new AsyncTaskResultListener() {
            @Override
            public void onProcessFinished(Result result) {
                if(result == Result.FAILED){
                    Toast.makeText(MainActivity.this, "Could not connect to server.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        registerEmployeeDeviceAsyncTask.execute();
    }

    private void sendUnsafeWorkday() {
        SendUnsafeWorkdayAsyncTask sendUnsafeWorkdayAsyncTask = new SendUnsafeWorkdayAsyncTask(DataHolder.getInstance().getUuid());
        sendUnsafeWorkdayAsyncTask.setAsyncTaskResultListener(new AsyncTaskResultListener() {
            @Override
            public void onProcessFinished(Result result) {
                if(result == Result.FAILED){
                    Toast.makeText(MainActivity.this, "Could not connect to server.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sendUnsafeWorkdayAsyncTask.execute();
    }

    private void setBreakAlarms(Date startTime, Date endTime){
        AlarmCreator alarmCreator = new AlarmCreator(getApplicationContext(), startTime, endTime);
        alarmCreator.execute();
    }


    @Override
    public void clockInButtonClicked(Date punchInTime) {
        DataHolder.getInstance().setTimsheetPunchInTime(punchInTime);
        if(DataHolder.getInstance().getTimesheet().getPunchOutTime() != null){
            DataHolder.getInstance().setTimsheetPunchOutTime(null);
        }
        sendPunchInToServer(punchInTime);
        if(DataHolder.getInstance().getSchedule() != null){

            Day d = DataHolder.getInstance().getCurrentDayFromSchedule();
            System.out.println(d.isActive() + " " + new SimpleDateFormat("M/d/yy h:mm.s").format(d.getStartTime()) + " " + new SimpleDateFormat("M/d/yy h:mm.s").format(d.getEndTime()));
            if(d.isActive()){
                setBreakAlarms(d.getStartTime(), d.getEndTime());
            }
        }
        showEnterJobDialog();
    }

    @Override
    public void clockOutButtonClicked(Date punchOutTime) {
        DataHolder.getInstance().setTimsheetPunchOutTime(punchOutTime);
        sendPunchOutToServer(punchOutTime);
        AlarmCreator.cancelAlarms(getApplicationContext());
        showInjuryDialog();
    }

    @Override
    public void jobButtonClicked() {
        showEnterJobDialog();
    }

    @Override
    public void settingsButtonClicked() {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    private void showEnterJobDialog(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("EnterJobDialogFragment");
        if(prev != null){
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        EnterJobDialogFragment frag = EnterJobDialogFragment.newInstance();
        frag.show(ft, "EnterJobDialogFragment");
    }

    private void showSelectEmployeeDialog(ArrayList<Employee> employees){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("EmployeeSelectDialog");
        if(prev != null){
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        SelectEmployeeDialogFragment frag = SelectEmployeeDialogFragment.newInstance(employees);
        frag.setCancelable(false);
        frag.show(ft, "EmployeeSelectDialog");
    }

    private void showInjuryDialog(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("InjuryDialog");
        if(prev != null){
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        SafeWorkDayDialogFragment frag = SafeWorkDayDialogFragment.newInstance();
        frag.show(ft, "InjuryDialog");
    }

    private void closeInjuryDialog(){
        Fragment prev = getSupportFragmentManager().findFragmentByTag("InjuryDialog");
        if(prev != null){
            ((SafeWorkDayDialogFragment) prev).dismiss();
        }
    }

    @Override
    public void onJobEntered(int jobNumber) {
        Fragment prev = getSupportFragmentManager().findFragmentByTag("EnterJobDialogFragment");
        if(prev != null){
            ((EnterJobDialogFragment) prev).dismiss();
        }
        Job job = new Job(jobNumber, Calendar.getInstance().getTime());
        DataHolder.getInstance().addTimesheetJob(job);
        sendJobToServer(job);
    }

    @Override
    public void onEmployeeSelected(Employee e) {
        //close dialog
        Fragment prev = getSupportFragmentManager().findFragmentByTag("EmployeeSelectDialog");
        if(prev != null){
            ((SelectEmployeeDialogFragment) prev).dismiss();
        }
        //create a timesheet from the selected employee
        DataHolder.getInstance().setTimesheet(new EmployeeTimesheet(e));
        // save the employees information to be reloaded.
        SharedPreferencesHandler sharedPreferencesHandler = new SharedPreferencesHandler(this);
        sharedPreferencesHandler.saveEmployeeInfo(DataHolder.getInstance().getTimesheet());
        sharedPreferencesHandler.setEmployeeSelected(true);
        registerDeviceWithServer();
    }

    @Override
    public void onEmployeesReceived(ArrayList<Employee> employees) {
        loadingPanel.setVisibility(View.GONE);
        if(employees != null){
            showSelectEmployeeDialog(employees);
        } else {
            Toast.makeText(this, "Could not get employees from server.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onScheduleReceived(ArrayList<Day> schedule) {
        if(schedule != null){
            DataHolder.getInstance().setSchedule(schedule);
        } else {
            Toast.makeText(this, "Could not get schedule from server.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTimesheetRecieved(EmployeeTimesheet timesheet) {
        if(timesheet != null){
            if(DataHolder.getInstance().getTimesheet().getJobs().size() > 0){
                timesheet.addAllJobs(DataHolder.getInstance().getTimesheet().getJobs());
            }
            DataHolder.getInstance().setTimesheet(timesheet);
        } else {
            loadEmployeeData();
        }
    }

    @Override
    public void onUnsafeWorkDay() {
        sendUnsafeWorkday();
        closeInjuryDialog();
    }

    @Override
    public void onSafeWorkDay() {
        closeInjuryDialog();
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ServerConnectionService.MSG_SCHEDULE_UPDATED:
                    ArrayList<Day> updatedSchedule = (ArrayList<Day>) msg.obj;
                    if(updatedSchedule != null){
                        DataHolder.getInstance().setSchedule(updatedSchedule);
                        Toast.makeText(MainActivity.this, "Schedule updated.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ServerConnectionService.MSG_SERVER_INFO_UPDATED:
                    if(msg.getData() != null){
                        NetworkDataHolder.setServerIP(msg.getData().getString("serverIP"));
                        NetworkDataHolder.setServerPort(msg.getData().getInt("serverPort"));
                        Toast.makeText(MainActivity.this, "Server information updated.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }
}

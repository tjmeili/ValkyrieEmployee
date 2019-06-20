package com.meilinger.tj.cssvalkyrieemployee_v2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.meilinger.tj.cssvalkyrieemployee_v2.R;
import com.meilinger.tj.cssvalkyrieemployee_v2.local.data.DataHolder;
import com.meilinger.tj.cssvalkyrieemployee_v2.local.data.listeners.EmployeeTimesheetListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import server.data.EmployeeTimesheet;
import server.data.Job;


public class TimesheetFragment extends Fragment implements EmployeeTimesheetListener{
    private static final String TAG = TimesheetFragment.class.getName();
    private EmployeeTimesheet mTimesheet = null;
    private ArrayList<Job> jobs;
    private static final String ARGS_TIMESHEET = "args_timesheet";
    private static final String FORMAT_DATE = "M/d/yy", FORMATE_TIME = "h:mm.ss a";
    private ListView timesheetListView = null;
    private TextView tvCurrentDate     = null;
    private TextView tvTotalHours      = null;
    private TextView tvStartTime       = null;
    private TextView tvStopTime        = null;
    private TimesheetListAdapter timesheetListAdapter = null;



    public TimesheetFragment() {
        // Required empty public constructor
    }

    public static TimesheetFragment newInstance(/*EmployeeTimesheet timesheet*/) {
        TimesheetFragment fragment = new TimesheetFragment();
        /*Bundle args = new Bundle();
        args.putSerializable(ARGS_TIMESHEET, timesheet);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if(getArguments() != null){
            mTimesheet = (EmployeeTimesheet) getArguments().getSerializable(ARGS_TIMESHEET);
        }*/
        mTimesheet = DataHolder.getInstance().getTimesheet();
        jobs = new ArrayList<>();
        DataHolder.getInstance().setTimesheetListenerTimesheetFrag(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vTimesheet = inflater.inflate(R.layout.fragment_timesheet, container, false);

        timesheetListView = vTimesheet.findViewById(R.id.timesheetListView);
        tvCurrentDate = (TextView) vTimesheet.findViewById(R.id.tvCurrentDate);
        tvStartTime = (TextView) vTimesheet.findViewById(R.id.tvStartTime);
        tvStopTime = (TextView) vTimesheet.findViewById(R.id.tvStopTime);
        tvTotalHours = (TextView) vTimesheet.findViewById(R.id.tvTotalHours);

        tvCurrentDate.setText(new SimpleDateFormat(FORMAT_DATE).format(Calendar.getInstance().getTime()));

        if(mTimesheet == null){
            mTimesheet = new EmployeeTimesheet();
        }
        initilizeListAdapter();
        return vTimesheet;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataHolder.getInstance().setTimesheetListenerTimesheetFrag(null);
    }

    private void initilizeListAdapter(){
        timesheetListAdapter = new TimesheetListAdapter(this.getContext(), R.layout.list_item_timesheet);
        timesheetListView.setAdapter(timesheetListAdapter);
    }

    @Override
    public void onEmployeeTimesheetUpdated(EmployeeTimesheet timesheet) {
        mTimesheet = timesheet;
        refreshViews();
    }

    private void refreshViews(){
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm.ss a");
        Date punchIn = mTimesheet.getPunchInTime();
        if(punchIn != null){
            tvStartTime.setText(formatter.format(punchIn));
            long totalHours = TimeUnit.MILLISECONDS.toHours(
                    Calendar.getInstance().getTime().getTime() - punchIn.getTime());
            tvTotalHours.setText("" + totalHours);
        }
        Date punchOut = mTimesheet.getPunchOutTime();
        if(punchOut != null){
            tvStopTime.setText(formatter.format(punchOut));
        }
        jobs.clear();
        jobs.addAll(mTimesheet.getJobs());
        timesheetListAdapter.notifyDataSetChanged();
    }



    private class TimesheetListAdapter extends ArrayAdapter<Job> {
        public TimesheetListAdapter(@NonNull Context context, int resource) {
            super(context, resource, jobs);
        }

        private class ViewHolder{
            TextView tvJobNumber, tvStartTime, tvEndTime;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull android.view.ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_timesheet, parent, false);
                holder = new ViewHolder();
                holder.tvJobNumber = (TextView) convertView.findViewById(R.id.tvJobNumber);
                holder.tvStartTime = (TextView) convertView.findViewById(R.id.tvStartTime);
                holder.tvEndTime = (TextView) convertView.findViewById(R.id.tvEndTime);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(holder != null){
                SimpleDateFormat formatter = new SimpleDateFormat("h:mm.ss a");
                if(holder.tvJobNumber != null){
                    String text = "";
                    if(mTimesheet.getJobs().get(position).getJobNumber() != -1){
                        text = mTimesheet.getJobs().get(position).getJobNumber() + "";
                    }
                    holder.tvJobNumber.setText(text);
                }
                if(holder.tvStartTime != null){
                    String text = "";
                    Date d = mTimesheet.getJobs().get(position).getStartTime();
                    if(d != null){
                        text = formatter.format(d);
                    }
                    holder.tvStartTime.setText(text);
                }
                if (holder.tvEndTime != null){
                    String text = "";
                    Date et = mTimesheet.getJobs().get(position).getEndTime();
                    if(et != null){
                        text = formatter.format(et);
                    }
                    holder.tvEndTime.setText(text);
                }
            }
            return convertView;
        }
    }

}

package com.meilinger.tj.cssvalkyrieemployee_v2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.meilinger.tj.cssvalkyrieemployee_v2.R;
import com.meilinger.tj.cssvalkyrieemployee_v2.local.data.DataHolder;
import com.meilinger.tj.cssvalkyrieemployee_v2.local.data.listeners.EmployeeTimesheetListener;

import java.util.Calendar;
import java.util.Date;

import server.data.EmployeeTimesheet;


public class HomeFragment extends Fragment implements EmployeeTimesheetListener{
    private static final String TAG = HomeFragment.class.getName();
    private Button clockInButton, clockOutButton, jobButton;

    private static final String ARGS_IS_CLOCKED_IN = "args_is_clocked_in", ARGS_NAME = "args_name";
    private OnHomeInteractionListener mListener;
    private String name = "";
    private boolean isPunchedIn = false;

    public static HomeFragment newInstance(/*EmployeeTimesheet timesheet*/) {
        HomeFragment fragment = new HomeFragment();
        /*if(timesheet != null){
            Bundle args = new Bundle();
            args.putBoolean(ARGS_IS_CLOCKED_IN, timesheet.getPunchOutTime() == null && timesheet.getPunchInTime() != null);
            args.putString(ARGS_NAME, timesheet.getFirstName() + timesheet.getLastName());
            fragment.setArguments(args);
        }*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if(getArguments() != null){
            name = getArguments().getString(ARGS_NAME);
            isPunchedIn = getArguments().getBoolean(ARGS_IS_CLOCKED_IN);
        }*/
        EmployeeTimesheet timesheet = DataHolder.getInstance().getTimesheet();
        name = timesheet.getFirstName() + " " + timesheet.getLastName();
        if(timesheet.getPunchOutTime() == null && timesheet.getPunchInTime() != null){
            isPunchedIn = true;
        }
        DataHolder.getInstance().setTimesheetListenerHomeFrag(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vHome = inflater.inflate(R.layout.fragment_home, container, false);
        clockInButton = (Button) vHome.findViewById(R.id.clockInButton);
        clockOutButton = (Button) vHome.findViewById(R.id.clockOutButton);
        jobButton = (Button) vHome.findViewById(R.id.jobButton);
        clockInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.clockInButtonClicked(Calendar.getInstance().getTime());
                }
                setButtonsClockedIn();
            }
        });

        clockOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.clockOutButtonClicked(Calendar.getInstance().getTime());
                }
                setButtonsClockedOut();
            }
        });

        jobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.jobButtonClicked();
                }
            }
        });
        if(isPunchedIn){
            setButtonsClockedIn();
        } else {
            setButtonsClockedOut();
        }
        vHome.findViewById(R.id.settingsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.settingsButtonClicked();
                }
            }
        });
        return vHome;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeInteractionListener) {
            mListener = (OnHomeInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataHolder.getInstance().setTimesheetListenerHomeFrag(null);
    }

    @Override
    public void onEmployeeTimesheetUpdated(EmployeeTimesheet timesheet) {
        if(timesheet != null){
            Log.i(TAG, "onEmployeeTimesheetUpdated: Updating views.");

            if(timesheet.getPunchOutTime() == null && timesheet.getPunchInTime() != null){
                setButtonsClockedIn();
            } else {
                setButtonsClockedOut();
            }
        }
    }

    public interface OnHomeInteractionListener {
        void clockInButtonClicked(Date punchInTime);
        void clockOutButtonClicked(Date punchOutTime);
        void jobButtonClicked();
        void settingsButtonClicked();
    }

    public void setButtonsClockedIn(){
        System.out.println("Setting buttons clocked IN");
        clockInButton.setEnabled(false);
        clockOutButton.setEnabled(true);
        jobButton.setEnabled(true);
    }

    public void setButtonsClockedOut(){
        System.out.println("Setting buttons clocked OUT");
        clockInButton.setEnabled(true);
        clockOutButton.setEnabled(false);
        jobButton.setEnabled(false);
    }
}

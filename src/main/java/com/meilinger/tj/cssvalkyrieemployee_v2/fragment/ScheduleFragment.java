package com.meilinger.tj.cssvalkyrieemployee_v2.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.meilinger.tj.cssvalkyrieemployee_v2.R;
import com.meilinger.tj.cssvalkyrieemployee_v2.local.data.DataHolder;
import com.meilinger.tj.cssvalkyrieemployee_v2.local.data.listeners.ScheduleListener;
import com.meilinger.tj.cssvalkyrieemployee_v2.view.ScheduleRowView;

import java.util.ArrayList;

import server.data.Day;



public class ScheduleFragment extends Fragment implements ScheduleListener{

    private static final String KEY_SCHEDULE = "key_schedule";

    private LinearLayout linearLayoutDayViewHolder;

    private ArrayList<Day> schedule = null;
    private ArrayList<ScheduleRowView> rows;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(/*ArrayList<Day> schedule*/) {
        ScheduleFragment fragment = new ScheduleFragment();
        /*Bundle args = new Bundle();
        args.putSerializable(KEY_SCHEDULE, schedule);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if(getArguments() != null){
            schedule = (ArrayList<Day>) getArguments().getSerializable(KEY_SCHEDULE);
        }*/
        schedule = DataHolder.getInstance().getSchedule();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vSchedule = inflater.inflate(R.layout.fragment_schedule, container, false);
        linearLayoutDayViewHolder = vSchedule.findViewById(R.id.linearLayout);
        initializeScheduleRows();
        DataHolder.getInstance().setScheduleListener(this);
        if(schedule == null){
            return vSchedule;
        }
        refreshViews();
        return vSchedule;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        DataHolder.getInstance().setScheduleListener(null);
    }

    @Override
    public void onScheduleUpdated(ArrayList<Day> schedule) {
        if(schedule != null){
            this.schedule = schedule;
            refreshViews();
        }
    }

    private void initializeScheduleRows(){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;
        rows = new ArrayList<>(7);
        for(int i = 0; i < 7; i++){
            LinearLayout l = new LinearLayout(getContext());
            l.setLayoutParams(lp);
            ScheduleRowView srv = new ScheduleRowView(getContext(), l);
            rows.add(srv);
            linearLayoutDayViewHolder.addView(l);
        }
    }

    private void refreshViews(){
        for(int i = 0; i < 7; i++){
            rows.get(i).setDay(schedule.get(i));
        }
    }
}

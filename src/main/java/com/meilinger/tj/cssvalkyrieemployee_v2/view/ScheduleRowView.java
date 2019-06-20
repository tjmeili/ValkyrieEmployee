package com.meilinger.tj.cssvalkyrieemployee_v2.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.meilinger.tj.cssvalkyrieemployee_v2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import server.data.Day;

/**
 * Created by TJ on 4/2/2018.
 */

public class ScheduleRowView extends View {

    private TextView tvStartTime, tvEndTime;
    private LinearLayout linearLayoutBreaks, llActive, llInactive;
    private ViewGroup root;
    private Day day;
    private ArrayList<Break> breaks;
    private SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");

    public ScheduleRowView(Context context, ViewGroup root) {
        super(context);
        this.root = root;
        init();
    }

    private void init(){
        breaks = new ArrayList<>();
        inflate(getContext(), R.layout.row_schedule, root);
        this.tvStartTime = (TextView) root.findViewById(R.id.tvStartTime);
        this.tvEndTime = (TextView) root.findViewById(R.id.tvEndTime);
        this.linearLayoutBreaks = (LinearLayout) root.findViewById(R.id.linearLayoutBreaks);
        this.llActive = (LinearLayout) root.findViewById(R.id.linearLayoutActive);
        this.llInactive = (LinearLayout) root.findViewById(R.id.linearLayoutInactive);
    }

    public void setDay(Day day){
        this.day = day;
        if(day != null){
            refreshViews();
        }
    }

    private void refreshViews(){
        Date startTime = day.getStartTime();
        Date endTime = day.getEndTime();
        if (day.isActive()){
            llActive.setVisibility(VISIBLE);
            llInactive.setVisibility(INVISIBLE);
        } else {
            llActive.setVisibility(INVISIBLE);
            llInactive.setVisibility(VISIBLE);
        }
        tvStartTime.setText(formatter.format(startTime));
        tvEndTime.setText(formatter.format(endTime));

        if(linearLayoutBreaks.getChildCount() > 0){
            linearLayoutBreaks.removeAllViews();
        }
        if(breaks != null && breaks.size() > 0){
            breaks.clear();
        }
        calculateBreaks(startTime, endTime);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;
        for(Break b : breaks){
            TextView tv = new TextView(getContext());
            String s = formatter.format(b.startBreak.getTime())
                    + " - "
                    + formatter.format(b.endBreak.getTime());
            tv.setText(s);
            tv.setLayoutParams(lp);
            linearLayoutBreaks.addView(tv);
        }
    }

    private void calculateBreaks(Date startTime, Date endTime){
        long diff = endTime.getTime() - startTime.getTime();
        diff = TimeUnit.MILLISECONDS.toHours(diff);

        Calendar c = Calendar.getInstance();
        c.setTime(startTime);

        if(diff <= 8 && diff >= 5){
            c.add(Calendar.HOUR_OF_DAY, 4);
            breaks.add(new Break(c, 20));
        } else if(diff <= 10){
            c.add(Calendar.HOUR_OF_DAY, 4);
            breaks.add(new Break(c, 20));
            c.add(Calendar.HOUR_OF_DAY, 2);
            breaks.add(new Break(c, 20));
        } else {
            c.add(Calendar.HOUR_OF_DAY, 4);
            breaks.add(new Break(c, 20));
            c.add(Calendar.HOUR_OF_DAY, 4);
            breaks.add(new Break(c, 40));
        }
    }

    private class Break{
        Calendar startBreak, endBreak;
        int duration;

        private Break(Calendar startBreak, int duration) {
            this.startBreak = Calendar.getInstance();
            this.endBreak = Calendar.getInstance();
            this.startBreak.setTime(startBreak.getTime());
            this.duration = duration;
            this.endBreak.setTime(startBreak.getTime());
            this.endBreak.add(Calendar.MINUTE, duration);
        }
    }
}

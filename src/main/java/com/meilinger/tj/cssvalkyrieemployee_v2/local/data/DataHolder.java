package com.meilinger.tj.cssvalkyrieemployee_v2.local.data;

import com.meilinger.tj.cssvalkyrieemployee_v2.local.data.listeners.EmployeeTimesheetListener;
import com.meilinger.tj.cssvalkyrieemployee_v2.local.data.listeners.ScheduleListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import server.data.Day;
import server.data.EmployeeTimesheet;
import server.data.Job;

public class DataHolder {
    private EmployeeTimesheet timesheet;
    private ArrayList<Day> schedule;

    private EmployeeTimesheetListener timesheetListenerHomeFrag = null, timesheetListenerTimesheetFrag = null;
    private ScheduleListener scheduleListener = null;

    public static DataHolder getInstance(){
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper{
        private static final DataHolder INSTANCE = new DataHolder();
    }

    private DataHolder(){
        timesheet = new EmployeeTimesheet();
    }

    public void setTimsheetPunchInTime(Date punchInTime){
        this.timesheet.setPunchInTime(punchInTime);
        notifyTimesheetUpdated();
    }

    public void setTimsheetPunchOutTime(Date punchOutTime){
        this.timesheet.setPunchOutTime(punchOutTime);
        notifyTimesheetUpdated();
    }

    public void addTimesheetJob(Job job){
        this.timesheet.addJob(job);
        notifyTimesheetUpdated();
    }

    public void setTimesheetInfo(String firstName, String lastName, UUID uuid){
        this.timesheet.setInfo(firstName, lastName, uuid);
        notifyTimesheetUpdated();
    }

    private void notifyTimesheetUpdated(){
        if(timesheetListenerHomeFrag != null){
            timesheetListenerHomeFrag.onEmployeeTimesheetUpdated(this.timesheet);
        }
        if(timesheetListenerTimesheetFrag != null){
            timesheetListenerTimesheetFrag.onEmployeeTimesheetUpdated(this.timesheet);
        }
    }

    private void notifyScheduleUpdated(){
        if(scheduleListener != null){
            scheduleListener.onScheduleUpdated(this.schedule);
        }
    }

    public void setTimesheetListenerHomeFrag(EmployeeTimesheetListener employeeTimesheetListener){
        this.timesheetListenerHomeFrag = employeeTimesheetListener;
    }

    public void setTimesheetListenerTimesheetFrag(EmployeeTimesheetListener employeeTimesheetListener){
        this.timesheetListenerTimesheetFrag = employeeTimesheetListener;
    }

    public void setScheduleListener(ScheduleListener scheduleListener){
        this.scheduleListener = scheduleListener;
    }

    public EmployeeTimesheet getTimesheet() {
        return timesheet;
    }

    public void setTimesheet(EmployeeTimesheet timesheet) {
        this.timesheet = timesheet;
        notifyTimesheetUpdated();
    }

    public UUID getUuid() {
        return timesheet.getEmployeeID();
    }

    public ArrayList<Day> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<Day> schedule) {
        this.schedule = schedule;
        notifyScheduleUpdated();
    }

    public Day getCurrentDayFromSchedule(){
        if(schedule != null){
            Calendar c = Calendar.getInstance();
            int dayOfWeek = 0;
            switch (c.get(Calendar.DAY_OF_WEEK)){
                case Calendar.WEDNESDAY:
                    dayOfWeek = 0;
                    break;
                case Calendar.THURSDAY:
                    dayOfWeek = 1;
                    break;
                case Calendar.FRIDAY:
                    dayOfWeek = 2;
                    break;
                case Calendar.SATURDAY:
                    dayOfWeek = 3;
                    break;
                case Calendar.SUNDAY:
                    dayOfWeek = 4;
                    break;
                case Calendar.MONDAY:
                    dayOfWeek = 5;
                    break;
                case Calendar.TUESDAY:
                    dayOfWeek = 6;
                    break;
            }
            return schedule.get(dayOfWeek);
        }
        return null;
    }
}

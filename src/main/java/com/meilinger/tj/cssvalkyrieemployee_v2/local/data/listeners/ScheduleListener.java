package com.meilinger.tj.cssvalkyrieemployee_v2.local.data.listeners;

import java.util.ArrayList;

import server.data.Day;

public interface ScheduleListener {
    void onScheduleUpdated(ArrayList<Day> schedule);
}

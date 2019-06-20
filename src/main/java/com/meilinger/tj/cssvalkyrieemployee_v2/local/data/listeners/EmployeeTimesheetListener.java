package com.meilinger.tj.cssvalkyrieemployee_v2.local.data.listeners;

import server.data.EmployeeTimesheet;

public interface EmployeeTimesheetListener {
    void onEmployeeTimesheetUpdated(EmployeeTimesheet timesheet);
}

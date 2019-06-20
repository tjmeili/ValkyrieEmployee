package com.meilinger.tj.cssvalkyrieemployee_v2.network.async_task;

public interface AsyncTaskResultListener {
    enum Result {
        SUCCESS, FAILED;
    }
    void onProcessFinished(Result result);
}

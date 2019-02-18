package com.nex.task;

public interface IHandlerTask {

    long getTimeStarted();
    void execute();
    boolean isFinished();

}

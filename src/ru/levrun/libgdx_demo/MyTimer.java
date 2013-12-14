package ru.levrun.libgdx_demo;

import com.badlogic.gdx.utils.TimeUtils;

public class MyTimer {
	private long start;
    private long secsToWait;

    public MyTimer(long secsToWait) {
        this.secsToWait = secsToWait;
    }

    public void start() {
        start = TimeUtils.millis() / 1000;
    }

    public boolean hasCompleted() {
        return TimeUtils.millis() / 1000 - start >= secsToWait;
    }
}

package cz.cvut.fel.cyber.dca.engine.util;

/**
 * Created by Jan on 10. 1. 2016.
 */
public class StopWatch {

    private long hh, mm, ss;
    private long tick, tock, out;
    private boolean isRunning;

    public StopWatch() {
        hh = 0;
        mm = 0;
        ss = 0;

        out = 0;

        tick = 0;

        tock = 0;

        setIsRunning(false);
    }

    /**
     * Watch starts ticking
     *
     */
    public synchronized void start() {

        tick = System.currentTimeMillis();
        setIsRunning(true);
    }

    /**
     * Watch stops ticking
     *
     */
    public synchronized void stop() {

        // Get current time in millis
        tock = System.currentTimeMillis();
        if (tick != 0) {
            out += tock - tick;
        }
        tick = 0;
        tock = 0;

        // Convert millis to hh:mm:ss time format
        ss = out / 1000;
        if (ss >= 60) {
            mm = ss / 60;
            ss = ss % 60;
            if (mm >= 60) {

                hh = mm / 60;
                mm = mm % 60;
            }
        }

        setIsRunning(false);
    }

    /**
     * Resets watch so that time is 00:00:00
     *
     */
    public synchronized void reset() {

        tick = 0;
        tock = 0;
        out = 0;
        setIsRunning(false);
    }

    /**
     * If is running return current time, else return time set by last stop in
     * millis
     *
     * @return time in millis
     */
    public long getTime() {
        if (isRunning()) {
            tock = System.currentTimeMillis();
            out += tock - tick;
        }
        return out;
    }

    private void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    /**
     *
     * @return true if StopWatch is running
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * @return time represented as String in "hh:mm:ss" format
     */
    public synchronized String getTimeAsText() {
        stop();
        start();
        String tmp = "";
        if (hh < 10) {
            tmp += "0" + Long.toString(hh);
        } else {
            tmp += Long.toString(hh);
        }
        tmp += ":";
        if (mm < 10) {
            tmp += "0" + Long.toString(mm);
        } else {
            tmp += Long.toString(mm);
        }
        tmp += ":";
        if (ss < 10) {
            tmp += "0" + Long.toString(ss);
        } else {
            tmp += Long.toString(ss);
        }
        return tmp;
    }

    public long getHours() {
        stop();
        start();
        return hh;
    }

    public long getMinutes() {
        stop();
        start();
        return mm;
    }

    public long getSeconds() {
        stop();
        start();
        return ss;
    }

}

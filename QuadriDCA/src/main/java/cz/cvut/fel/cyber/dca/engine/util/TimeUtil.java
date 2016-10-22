package cz.cvut.fel.cyber.dca.engine.util;

/**
 * Created by Jan on 20.10.2016.
 */
public class TimeUtil {

    public static String getTimeFromMillis(int millis){
        int ss = millis/1000;
        int mils = millis%(ss*1000);
        int mm = 0;
        int hh = 0;

        if (ss >= 60) {
            mm = ss / 60;
            ss = ss % 60;
            if (mm >= 60) {
                hh = mm / 60;
                mm = mm % 60;
            }
        }

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
        String milsString;
        if((mils < 10)){
            milsString = ",00" +  Long.toString(mils);
        }else if((mils >= 10)&&(millis < 100)){
            milsString = ",0" +  Long.toString(mils);
        }else{
            milsString = "," +  Long.toString(mils);
        }
        tmp += milsString;
        return tmp;
    }

}

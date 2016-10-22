package cz.cvut.fel.cyber.dca.engine.gui;


import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.util.StopWatch;
import cz.cvut.fel.cyber.dca.engine.util.TimeUtil;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.time.LocalTime;
import java.util.List;

/**
 * Created by Jan on 18. 10. 2015.
 */
public class ServiceLogger {

    private static String TEXT = "";
    private static TextArea AREA = null;
    private static List<Label> LABEL_LIST = null;
    private static int MAX_LINE_COUNT = 500;
    private static Label SIM_TIME_LABEL = null;
    public static boolean LOG_ON = true;

    public static void clear(){
        if(AREA!=null)AREA.setText("");
    }

    public static void register(TextArea ta){
        AREA = ta;
    }

    public static void registerLabelList(List<Label> list){
        LABEL_LIST = list;
    }

    public static void registerSimTimeLabel(Label label){
        SIM_TIME_LABEL = label;
    }

    public static void log(String message){
        if(!LOG_ON)return;

        if(TEXT.split(System.getProperty("line.separator")).length>=MAX_LINE_COUNT){
            String[] lines = TEXT.split(System.getProperty("line.separator"));
            TEXT = "";
            for(int i = 0; i < 20; i++)TEXT += lines[i] + System.getProperty("line.separator");
        }

        LocalTime time = LocalTime.now();
        TEXT = message + System.getProperty("line.separator")  + TEXT;
        if(AREA !=null){
            Platform.runLater(() -> {
                AREA.setText(TEXT);
            });
            }
    }

    public static void logLabel(Quadrotor unit, String text){
        if(LABEL_LIST!=null){
            if(unit.getId()<LABEL_LIST.size()){
                Platform.runLater(() -> {
                    LABEL_LIST.get(unit.getId()).setText(Integer.toString(unit.getId()) + ": " + text);
                });
            }
        }
    }

    public static void logSimTime(){
        if(SIM_TIME_LABEL!=null){
            Platform.runLater(() -> {
                SIM_TIME_LABEL.setText("Sim time: " + TimeUtil.getTimeFromMillis(Experiment.CURRENT_SIMULATION_MILLIS) + " s.");
            });
        }
    }


}

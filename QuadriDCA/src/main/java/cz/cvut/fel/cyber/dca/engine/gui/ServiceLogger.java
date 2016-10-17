package cz.cvut.fel.cyber.dca.engine.gui;


import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
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
    public static boolean LOG_ON = true;

    public static void register(TextArea ta){
        AREA = ta;
    }

    public static void registerLabelList(List<Label> list){
        LABEL_LIST = list;
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
            Platform.runLater(() -> {
                LABEL_LIST.get(unit.getId()).setText(Integer.toString(unit.getId()) + ": " + text);
            });
        }
    }



}

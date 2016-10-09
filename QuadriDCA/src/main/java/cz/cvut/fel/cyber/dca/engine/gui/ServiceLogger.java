package cz.cvut.fel.cyber.dca.engine.gui;


import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.time.LocalTime;

/**
 * Created by Jan on 18. 10. 2015.
 */
public class ServiceLogger {

    private static String text = "";
    private static TextArea area = null;

    public static void register(TextArea ta){
        area = ta;
    }

    public static void log(String message){
        LocalTime time = LocalTime.now();
        text=message +System.getProperty("line.separator")  + text ;
        if(area!=null){
            Platform.runLater(() -> {
                area.setText(text);
            });
            }
    }

}

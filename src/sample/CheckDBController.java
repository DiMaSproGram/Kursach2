package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class CheckDBController {
    @FXML private ProgressIndicator progInd;
    @FXML private Label lab;
    WaitThread waitThread = new WaitThread();

    public void initialize() {
        progInd.setProgress(-1.0);
    }
    public void checkDB(Stage stage) throws IOException, SQLException, ClassNotFoundException {
        Parser parser = new Parser();
        if (parser.isStart())
            if (parser.netIsAvailable()){
                stage.close();
                new ParsingController().setScene("parsing.fxml", "PC-creator");
            }
            else
                new ErrorInterController().setScene("internConnFrame.fxml", "Error", stage);
        else {
            stage.close();
            new Controller().setScene("sample.fxml", "PC-creator");
        }
    }
    public static void wait(int sec) {
        LocalDateTime localDateTime = LocalDateTime.now();
        while (compareTime(LocalDateTime.now(), localDateTime.plusSeconds(sec)) < 0 ){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static int compareTime(LocalDateTime now, LocalDateTime other) {
        int cmp = (now.getHour() - other.getHour());
        if (cmp == 0) {
            cmp = (now.getMinute() - other.getMinute());
            if (cmp == 0) {
                cmp = (now.getSecond() - other.getSecond());
            }
        }
        return cmp;
    }

    class WaitThread extends Thread {
        @Override
        public void run() {
            CheckDBController.this.wait(10);
            this.stop();
        }
    }

}

package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ParsingController {
    @FXML private Button but;
    @FXML private ProgressBar progresBar;
    private ProgresBarThread pbThread = new ProgresBarThread();
    private ParserThread parsThread = new ParserThread();
    private Parser parser;

    public void initialize() throws SQLException, IOException, ClassNotFoundException {
        progresBar.setProgress(0.0);
        but.setDisable(true);
        parser = new Parser();
        parsThread.start();
        pbThread.start();
    }
    @FXML
    public void click(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
        new Controller().setScene("sample.fxml","PC-creator");

    }
    @FXML
    public void mousMove(){
        if (!parsThread.isAlive())
            but.setDisable(false);
    }
    public void setScene(String fxmlFile, String title) throws IOException {
        Parent root2 = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage newWindow = new Stage();
        newWindow.setTitle(title);
        newWindow.setScene(new Scene(root2, 1080, 600));
        newWindow.show();

    }

    class ParserThread extends Thread {
        @Override
        public void run() {
            try {
                parser.startParsing();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            this.interrupt();
        }
    }
    class ProgresBarThread extends Thread {
        @Override
        public void run() {
            while (parser.getProgress() <= 1) {
                progresBar.setProgress(parser.getProgress());
            }
            this.interrupt();
        }
    }
}



package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class ErrorInterController {
    @FXML private Label lb1;

    public void initialize() {
    }
    @FXML
    private void retry(ActionEvent event) throws InterruptedException, IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
        if (!Parser.netIsAvailable()) {
            Thread.sleep(2000);
            stage.show();
        }
        else {
            Stage parntStg = (Stage) stage.getOwner();
            parntStg.close();
            new ParsingController().setScene("parsing.fxml", "PC-creator");
        }
    }
    @FXML
    private void contin(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
        Stage parntStg = (Stage) stage.getOwner();
        parntStg.close();
        new Controller().setScene("sample.fxml", "PC-creator");
    }
    public void setScene(String fxmlFile, String title, Stage stage) throws IOException {
        System.out.println(stage.getTitle());

        Parent root2 = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage newWindow = new Stage();
        newWindow.setTitle(title);
        newWindow.setScene(new Scene(root2, 600, 400));
        newWindow.initModality(Modality.WINDOW_MODAL);
        newWindow.initOwner(stage);
        newWindow.show();
    }
}


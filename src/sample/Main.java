package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        CheckDBController checkDBController = new CheckDBController();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("checkFrame.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("PC-creator");
        primaryStage.setScene(new Scene(root, 1080, 600));
        primaryStage.show();
        checkDBController.checkDB(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }

}

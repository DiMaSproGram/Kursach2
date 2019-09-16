package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;

public class Controller {
    @FXML private ChoiceBox<String> choiceBox;
    @FXML private TextField textField;
    @FXML private TextField txtCpu;
    @FXML private TextField txtVidCard;
    @FXML private TextField txtMothBoard;
    @FXML private TextField txtRam;
    @FXML private TextField txtHdd;
    @FXML private TextField txtSsd;
    @FXML private TextField txtPowSup;
    @FXML private TextField txtCool;
    @FXML private TextField txtCompCase;
    @FXML private TextField totalPrice;
    @FXML private ImageView imageView1;

    public void initialize(){
        ObservableList<String> goals = FXCollections.observableArrayList("Games", "Work");
        choiceBox.setItems(goals);
        choiceBox.setValue("Games");
        totalPrice.setOnKeyTyped(Event::consume);
    }
    public void setScene(String fxmlFile, String title) throws IOException {
        Parent root2 = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage newWindow = new Stage();
        newWindow.setTitle(title);
        newWindow.setScene(new Scene(root2, 1920, 1020));
        newWindow.show();
    }
    @FXML
    private void click() throws SQLException, ClassNotFoundException, IOException {
        if(Integer.parseInt(textField.getText()) < 700)
            textField.setText("700");
        CreationPC creatPC;
        CreationPC.Goals goal;

        switch (choiceBox.getValue()) {
            case "Work": goal = CreationPC.Goals.FOR_WORK;
            break;
            case "Games": goal = CreationPC.Goals.FOR_GAMES;
            break;
            default: goal = CreationPC.Goals.FOR_GAMES;
        }
        System.out.println(textField.getText());
        creatPC = new CreationPC(Double.parseDouble(textField.getText()), goal);
        Computer comp = creatPC.start();
        txtCpu.setText(comp.getCpuTitle());
        txtVidCard.setText(comp.getVidCardTitle());
        txtMothBoard.setText(comp.getMothBoardTitle());
        txtRam.setText(comp.getRamTitle());
        txtHdd.setText(comp.getHddTitle());
        txtSsd.setText(comp.getSsdTitle());
        txtPowSup.setText(comp.getPowSuppTitle());
        txtCool.setText(comp.getCoolTitle());
        totalPrice.setText(Double.toString(comp.getTotalPrice()));
        txtCompCase.setText(comp.getCompCaseTitle());
        System.out.println(toInputStream(comp.getCompCaseImg()));
        Image image = new Image(toInputStream(comp.getCompCaseImg()));
        System.out.println(image);
        imageView1.setImage(image);
    }
    @FXML
    private void keyPress() {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(textField.getText().length() > 5)
                textField.setText(textField.getText(0,5));
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    @FXML
    private void keyReleased() {
        if(Integer.parseInt(textField.getText()) > 40000)
            textField.setText("40000");
    }

    private InputStream toInputStream(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrOut = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrOut);
        InputStream byteArrIn = new ByteArrayInputStream(byteArrOut.toByteArray());

        return  byteArrIn;
    }
}

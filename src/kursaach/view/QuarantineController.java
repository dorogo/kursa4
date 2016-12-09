/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kursaach.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kursaach.MainApp;

/**
 *
 * @author user
 */
public class QuarantineController implements Initializable {

    private MainApp mainApp;
    private GenerateController genWindow;
    private QuarantineController currWindow;
    public String filtredSymbols;

    @FXML
    private Button startBtn;
    @FXML
    private Button generateFileBtn;
    @FXML
    private Button pauseBtn;
    @FXML
    private Button stopBtn;
    @FXML
    public TextField speedTxt;
    @FXML
    public TextArea console1Txt;
    @FXML
    public TextArea console2Txt;
    @FXML
    public TextArea console1_1Txt;
    @FXML
    public TextArea console2_1Txt;
    @FXML
    public TextArea bufferTxt;
    @FXML
    public TextField sharedCharTxt;
    @FXML
    public TextArea resultTxt;
    @FXML
    private Pane modesPane;
    @FXML
    private Pane speedPane;
    @FXML
    public RadioButton mode1Rd;
    @FXML
    public RadioButton mode2Rd;
    @FXML
    public Label bufferLb;
    @FXML
    public Label sharedCharLb;

    @FXML
    public void onM1Toggle(MouseEvent ev) {
        bufferTxt.setDisable(true);
        bufferLb.setDisable(true);
        sharedCharTxt.setDisable(false);
        sharedCharLb.setDisable(false);
    }

    @FXML
    public void onM2Toggle(MouseEvent ev) {
        bufferTxt.setDisable(false);
        bufferLb.setDisable(false);
        sharedCharTxt.setDisable(true);
        sharedCharLb.setDisable(true);
    }

    @FXML
    public void onKeyTyped(KeyEvent ev) {
        String c = ev.getCharacter();
        if (!c.matches("[0-9]+")) {
            ev.consume();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filtredSymbols = "[\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\_\\+\\-\\=\\{\\}]";
        currWindow = this;
        bufferTxt.setDisable(true);
        bufferLb.setDisable(true);
        stopBtn.setDisable(true);
        pauseBtn.setDisable(true);
        startBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                speedPane.setDisable(true);
                modesPane.setDisable(true);
                startBtn.setDisable(true);
                stopBtn.setDisable(false);
                pauseBtn.setDisable(false);
                generateFileBtn.setDisable(true);
                try {
                    mainApp.startProcessQuarantine(mode2Rd.isSelected());
                } catch (IOException ex) {
                    Logger.getLogger(QuarantineController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        pauseBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (pauseBtn.getText().equals("pause")) {
                    speedPane.setDisable(false);
                    pauseBtn.setText("contine");
                } else {
                    speedPane.setDisable(true);
                    pauseBtn.setText("pause");
                }
                mainApp.changeStateQua();
            }
        });
        stopBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    mainApp.stopQuarantine();
                } catch (InterruptedException ex) {
                    Logger.getLogger(QuarantineController.class.getName()).log(Level.SEVERE, null, ex);
                }

                stopBtn.setDisable(true);
                pauseBtn.setDisable(true);
                speedPane.setDisable(false);
                modesPane.setDisable(false);
                startBtn.setDisable(false);
                pauseBtn.setText("pause");
                generateFileBtn.setDisable(false);

                bufferTxt.setText("");
                console1Txt.setText("");
                console2Txt.setText("");
                sharedCharTxt.setText("");
            }
        });
        generateFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("GEnerate window ihs here!");
                Parent root;
                try {

                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(MainApp.class.getResource("view/GenerateFiles.fxml"));
                    root = loader.load();

                    Stage stage = new Stage();
                    stage.setTitle("Generate files for quarantine.");
                    stage.setScene(new Scene(root, 800, 400));
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(mainApp.getPrimaryStage());
                    stage.setResizable(false);
                    stage.show();

                    GenerateController controller = loader.getController();

                    controller.setParentApp(currWindow);

                    // Hide this current window (if this is what you want)
//                    ((Node) (event.getSource())).getScene().getWindow().s
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public int getQuSpeed() {
        return Integer.parseInt(speedTxt.getText());
    }

    public void setMainApp(MainApp mApp) {
        this.mainApp = mApp;

    }

    public void setFiltredSymbols(String s) {
        this.filtredSymbols = s;
    }

}

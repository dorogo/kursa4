/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kursaach.view;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import kursaach.MainApp;
import kursaach.RSA.RSA;

/**
 *
 * @author user
 */
public class RegLogController implements Initializable {
    
    private final String[] listErr = {
        " ",
        "This login is already in use.",
        "Login or password is wrong."};
    private int result = 0;
    private int[] keys = {};
    private MainApp mainApp;
    private String packetString;

    @FXML
    private Button regBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private TextField nameRegTxt;

    @FXML
    private TextField passRegTxt;

    @FXML
    private TextField passConfirmRegTxt;
    
    @FXML
    private TextField nameLgTxt;

    @FXML
    private TextField passLgTxt;

    @FXML
    private Label errRegLb;

    @FXML
    private Label errLgLb;

    @FXML
    private Label goodRegLb;

    @FXML
    private Label goodLgLb;
    
    @FXML 
    public void processKeyEvent(KeyEvent ev) {
        String c = ev.getCharacter();
        if(!c.matches("[A-Za-z0-9@$&_]+")) ev.consume();
    }
    @FXML 
    public void processKey2Event(KeyEvent ev) {
        String c = ev.getCharacter();
        if(!c.matches("[A-Za-z0-9!@#$%^&*+=-_]+")) ev.consume();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        packetString = "";
        
        regBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                
                goodRegLb.setText("");
                if (nameRegTxt.getText().length() == 0 || passRegTxt.getText().length() == 0) {
                    errRegLb.setText("Please fill all fields.");
                } else if (passRegTxt.getText().equals(passConfirmRegTxt.getText())) {
                    //RSA encrypt
                    packetString = nameRegTxt.getText() + " " + passRegTxt.getText();
                    packetString = RSA.encryptRSA(packetString, keys[0], keys[1]);
                    try {
                        result = mainApp.registration(packetString);
                    } catch (IOException ex) {
                        Logger.getLogger(RegLogController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    errRegLb.setText(getErr(result));
                    if (result == 0) {
                        goodRegLb.setText("Registration completed!");
                        nameRegTxt.setText("");
                        passRegTxt.setText("");
                        passConfirmRegTxt.setText("");
                        //clear fields
                    }
                } else {
                    errRegLb.setText("Passwords do not match.");
                }
                System.out.println("Registred.");
                System.out.println("name:"+nameRegTxt.getText());
                System.out.println("pass:"+passRegTxt.getText());
                System.out.println("passConfitm:"+passConfirmRegTxt.getText());
                

            }
        });
        
        loginBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                goodLgLb.setText("");
                try {
                    packetString = nameLgTxt.getText() + " " + passLgTxt.getText();
                    System.out.println(".handle()" + packetString);
                    packetString = RSA.encryptRSA(packetString, keys[0], keys[1]);
                    
                    System.out.println(".handle()" + packetString);
                    result = mainApp.authorization(packetString);
                } catch (IOException ex) {
                    Logger.getLogger(RegLogController.class.getName()).log(Level.SEVERE, null, ex);
                }
                errLgLb.setText(getErr(result));
                if (result == 0) {
                    //swap screen
                    goodLgLb.setText("Loged in!");
                    nameLgTxt.setText("");
                    passLgTxt.setText("");
                    mainApp.runQuarantine();
                }
                System.out.println("Loged in.");
                System.out.println("name:"+nameLgTxt.getText());
                System.out.println("pass:"+passLgTxt.getText());

            }
        });

    }
    
    private String getErr (int code) {
        return listErr[code];
    } 

    public void setMainApp(MainApp mApp) {
        this.mainApp = mApp;

    }
    
    public void setKeysRSA (int[] arr) {
        keys = arr;
    } 

}

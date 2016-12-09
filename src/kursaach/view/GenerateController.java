/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kursaach.view;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import kursaach.quarantinelab.QuarantineLab;
import kursaach.utils.FileHelper;

/**
 *
 * @author user
 */
public class GenerateController implements Initializable {
    
    private QuarantineController parentWindow;

    @FXML
    TextArea consoleTxt;
    @FXML
    TextArea alphTxt;
    @FXML
    TextArea filterTxt;
    @FXML
    TextField countTxt;
    @FXML
    Button backBtn;
    @FXML
    Button genBtn;

    @FXML
    public void onKeyTypedFilter(KeyEvent ev) {
        String c = ev.getCharacter();
        if (c.matches("[" + alphTxt.getText() + "\\]\\[]+")) {
            ev.consume();
        }
    }

    @FXML
    public void onKeyTypedCount(KeyEvent ev) {
        String c = ev.getCharacter();
        if (!c.matches("[0-9]+")) {
            ev.consume();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage st = (Stage) backBtn.getScene().getWindow();
                st.close();
            }
        });
        genBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                consoleTxt.setText("Generating text...\n"
                        + "alphabet:"+alphTxt.getText()
                        + "\n'dirt' symbols:"+filterTxt.getText() + "\n");
                FileHelper.writeString(QuarantineLab.generateString(new SecureRandom(), (alphTxt.getText() + filterTxt.getText()), Integer.parseInt(countTxt.getText())), QuarantineLab.pathSrcText, false);
                String tmp = "";
                try {
                    tmp = FileHelper.getString(QuarantineLab.pathSrcText);
                } catch (IOException ex) {
                    Logger.getLogger(GenerateController.class.getName()).log(Level.SEVERE, null, ex);
                }
                consoleTxt.appendText("Source text generated successfully.\n"  
                        + "Generating clean source text file...\n");
                String result = "";
                int l = tmp.length();
                char c;
                System.out.println(""+filterTxt.getText());
                String filterString = "[";
                for (int i = 0; i < filterTxt.getText().length(); i++) {
                    if (!String.valueOf(filterTxt.getText().charAt(i)).matches("[A-Za-z0-9]"))
                        filterString += "\\";
                    filterString += filterTxt.getText().charAt(i);
                }
                filterString += "]";
                for (int j = 0; j < l; j++) {
                    c = tmp.charAt(j);
                    if (!Character.toString(c).matches( filterString )) {
                        result += c;
                    }
                }
                consoleTxt.appendText("Source clean text generated successfully.\n\n");
                System.out.println(tmp.length() + " - " + result.length());
                System.out.println("Source text:\n" + tmp); 
                System.out.println("Source clean text:\n" + result); 
                System.out.println("Filtred symbols: " + filterString.replaceAll("[\\[\\]\\\\]", "")); 
                FileHelper.writeString(result, QuarantineLab.pathCleanSrcText, false);
                parentWindow.setFiltredSymbols(filterString);

            }
        });
    }

    public void setParentApp(QuarantineController q) {
        parentWindow = q;
    }

}

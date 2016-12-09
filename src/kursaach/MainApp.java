/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kursaach;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import kursaach.RSA.RSA;
import kursaach.view.RegLogController;
import kursaach.labauthorization.LabAuthorization;
import kursaach.view.QuarantineController;
import kursaach.quarantinelab.QuarantineLab;

/**
 *
 * @author user
 */
public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private LabAuthorization regLogManager;
    private QuarantineLab quarantineManager;

    public MainApp() {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Kursach.");

        initRootLayout();
//        runQuarantine();
        showRegLogScreen();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRegLogScreen() {
        RSA.getInstance().genKeys();
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RegLogLayout.fxml"));
            AnchorPane screen = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(screen);

            // Give the controller access to the main app.
            RegLogController controller = loader.getController();
            controller.setMainApp(this);

            controller.setKeysRSA(RSA.getInstance().getPublicKeys());
            regLogManager = new LabAuthorization();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int authorization(String packetString) throws IOException {
        //decode
        String decryptedString = RSA.decryptRSA(packetString, RSA.getInstance().getSecretKeys()[0], RSA.getInstance().getSecretKeys()[1]);
        return regLogManager.authorize(decryptedString.split(" ")[0], decryptedString.split(" ")[1]);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public int registration(String packetString) throws IOException {
        String decryptedString = RSA.decryptRSA(packetString, RSA.getInstance().getSecretKeys()[0], RSA.getInstance().getSecretKeys()[1]);
        return regLogManager.registrate(decryptedString.split(" ")[0], decryptedString.split(" ")[1]);
    }

    public void runQuarantine() {
        System.out.println("NEXT STEP");
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Quarantine.fxml"));
            AnchorPane screen = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(screen);

            // Give the controller access to the main app.
            QuarantineController controller = loader.getController();
            controller.setMainApp(this);

            quarantineManager = new QuarantineLab(controller);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startProcessQuarantine(boolean mode) throws IOException {
        quarantineManager.processQuarantine(mode);
    }

    public void changeStateQua() {
        quarantineManager.toggleRunning();
    }

    public void stopQuarantine() throws InterruptedException {
        quarantineManager.stopProcess();
    }

}

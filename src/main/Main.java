package main;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.stage.Stage;


/** The main class creates an application for inventory management and adds sample data. */
public class Main extends Application {

    private static Locale defineLocale() {
        Locale locale = Locale.getDefault();
        switch (locale.getLanguage()) {
            case "en":
            case "fr":
                break;
            default:
                locale = new Locale("en", "US");
        }
        return locale;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //ResourceBundle resources = ResourceBundle.getBundle("language.login", Locale.getDefault());
        //Parent root = FXMLLoader.load(getClass().getResource("../views/LoginScreen.fxml") ,resources);
        //primaryStage.setScene(new Scene(root, 300, 275));
        ResourceBundle resources = ResourceBundle.getBundle("login", defineLocale());
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("../views/LoginScreen.fxml"), resources);
        Scene scene = new Scene(fxmlLoader.load(), 300, 275);
        //scene.setRoot(fxmlLoader.load());
        primaryStage.setTitle("Inventory Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public static void main(String[] args) throws Exception {

        JDBC.startConnection();
        launch(args);
        JDBC.closeConnection();
    }

}

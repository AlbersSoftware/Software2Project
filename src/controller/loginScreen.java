package controller;

import DataAccessObj.appointmentAccess;
import Models.Appointments;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

import static DataAccessObj.userAccess.validateUser;

/**
 * This class has methods to validate and update user login, as well as to set location and language.
 */

public class loginScreen implements Initializable  {
    @FXML private Button cancelButton;
    @FXML private Button loginButton;
    @FXML private TextField loginScreenLocationField;
    @FXML private TextField loginScreenPassword;
    @FXML private TextField loginScreenUsername;
    @FXML private Label passwordField;
    @FXML private Label usernameField;
    @FXML private Label loginField;
    @FXML private Button cancelButtonField;
    @FXML private Label locationText;
    //@FXML
   // private Label zoneLabel;
    Stage stage;
    /**
     *  method for displaying alerts
     * @param alertType displays different alert types and returns so user cant input invalid information
     */
    private void displayAlert(int alertType) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        Alert alertwarn = new Alert(Alert.AlertType.WARNING);
        Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        ResourceBundle rb = ResourceBundle.getBundle("language/login", Locale.getDefault());
        switch (alertType) {
            case 1: alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Confirmation");
                alert.setContentText("No upcoming appointments.");
                alert.showAndWait();
                break;
            case 2: alert.setTitle(rb.getString("Error"));
                alert.setContentText(rb.getString("Incorrect"));
                alert.show();
                break;



        }}

    /**
     * This method controls the login button for the application, defines variables for 15 minute appointment check, validates the login,
     * location, activity log and language.
     * @param event representation of the action when a button is pressed.
     * @throws SQLException general sql exception .getAllAppointments
     * @throws IOException Signals that an I/O exception of some sort has occurred

     */

    @FXML
    private void loginButton(ActionEvent event) throws SQLException, IOException {
        try {

            ObservableList<Appointments> getAllAppointments = appointmentAccess.getAllAppointments();
            LocalDateTime currentTimeMinus15Min = LocalDateTime.now().minusMinutes(15);
            LocalDateTime currentTimePlus15Min = LocalDateTime.now().plusMinutes(15);
            LocalDateTime startTime;
            int getAppointmentID = 0;
            LocalDateTime displayTime = null;
            boolean appointmentWithin15Min = false;

            ResourceBundle rb = ResourceBundle.getBundle("language/login", Locale.getDefault());

            String usernameInput = loginScreenUsername.getText();
            String passwordInput = loginScreenPassword.getText();
            int userId = validateUser(usernameInput, passwordInput);

            FileWriter fileWriter = new FileWriter("login_activity.txt", true);
            PrintWriter outputFile = new PrintWriter(fileWriter);

            if (userId > 0) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/MainScreen.fxml"));
                Parent root = loader.load();
                stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();


                outputFile.print("user: " + usernameInput + " successfully logged in at: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");


                for (Appointments appointment : getAllAppointments) {
                    startTime = appointment.getStart();
                    if ((startTime.isAfter(currentTimeMinus15Min) || startTime.isEqual(currentTimeMinus15Min)) && (startTime.isBefore(currentTimePlus15Min) || (startTime.isEqual(currentTimePlus15Min)))) {
                        getAppointmentID = appointment.getAppointmentID();
                        displayTime = startTime;
                        appointmentWithin15Min = true;
                    }
                }
                if (appointmentWithin15Min != false) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment within 15 minutes: " + getAppointmentID + " and appointment start time of: " + displayTime);
                    Optional<ButtonType> confirmation = alert.showAndWait();

                } else {
                    displayAlert(1);

                }
            } else if (userId < 0) {
                displayAlert(2);


                outputFile.print("user: " + usernameInput + " failed login attempt at: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");

            }
            outputFile.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }



    }
    /**
     * This method just closes the application
     * @param event representation of the action when a button is pressed.
     */
    public void cancelButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * This method initializes the main screen and gets location info and sets text fields and language.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known
     * @param rb The resources used to localize the root object, or null if the root object was not localized
     */

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        try
        {

            Locale locale = Locale.getDefault();
            Locale.setDefault(locale);

            ZoneId zone = ZoneId.systemDefault();


            loginScreenLocationField.setText(String.valueOf(zone));

            rb = ResourceBundle.getBundle("language/login", Locale.getDefault());
            loginField.setText(rb.getString("Login"));
            usernameField.setText(rb.getString("username"));
            passwordField.setText(rb.getString("password"));
            loginButton.setText(rb.getString("Login"));
            cancelButtonField.setText(rb.getString("Exit"));
            locationText.setText(rb.getString("Location"));

        } catch(MissingResourceException e) {
            System.out.println("Resource file missing: " + e);
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }



}

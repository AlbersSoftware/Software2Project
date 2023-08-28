package controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class simply changes views based on button clicks on the main screen
 */
public class MainScreenController {
    @FXML private Button mainMenuAppointmentClick;
    @FXML private Button mainMenuCustomerClick;
    @FXML private Button mainMenuExitClick;
    @FXML private Button mainMenuReportsClick;

    /**
     * this method changes the scene
     * @param event representation of the action when a button is pressed.
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @FXML
    void mainMenuAppointmentClick(ActionEvent event) throws IOException {

        Parent appointmentMenu = FXMLLoader.load(getClass().getResource("../views/appointments.fxml"));
        Scene scene = new Scene(appointmentMenu);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    /**
     * this method changes the scene
     * @param event representation of the action when a button is pressed.
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @FXML
    void mainMenuCustomerClick(ActionEvent event) throws IOException {

        Parent customerMenu = FXMLLoader.load(getClass().getResource("../views/customer.fxml"));
        Scene scene = new Scene(customerMenu);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    /**
     * this method changes the scene
     * @param event representation of the action when a button is pressed.
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @FXML
    void mainMenuReportsClick(ActionEvent event) throws IOException {

        Parent reportMenu = FXMLLoader.load(getClass().getResource("../views/reports.fxml"));
        Scene scene = new Scene(reportMenu);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * This method closes the application
     * @param ExitButton button to close application
     */
    public void mainMenuExitClick(ActionEvent ExitButton) {
        Stage stage = (Stage) ((Node) ExitButton.getSource()).getScene().getWindow();
        stage.close();
    }


}

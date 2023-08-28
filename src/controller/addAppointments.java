package controller;
import DataAccessObj.appointmentAccess;
import DataAccessObj.contactAccess;
import DataAccessObj.customerAccess;
import DataAccessObj.userAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.JDBC;
import Models.Appointments;
import Models.Contacts;
import Models.Customers;
import Models.Users;

import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static main.timeUtil.*;
import static main.timeUtil.convertTimeDateLocal;

/**
 *  class to add appointments and display alerts
 */
public class addAppointments {

    @FXML
    private TextField addAppointmentCustomerID;
    @FXML
    private TextField addAppointmentUserID;
    @FXML
    private TextField addAppointmentDescription;
    @FXML
    private DatePicker addAppointmentEndDate;
    @FXML
    private ComboBox<String> addAppointmentEndTime;
    @FXML
    TextField addAppointmentID;
    @FXML
    private TextField addAppointmentLocation;
    @FXML
    Button addAppointmentSave;
    @FXML
    private DatePicker addAppointmentStartDate;
    @FXML
    private ComboBox<String> addAppointmentStartTime;
    @FXML
    private TextField addAppointmentTitle;
    @FXML
    private ComboBox<String> addAppointmentContact;
    @FXML
    Button addAppointmentsCancel;
    @FXML
    private TextField addAppointmentType;

    /**
     *  method for displaying alerts
     * @param alertType displays different alert types and returns so user cant input invalid information
     */
    private void displayAlert(int alertType) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        Alert alertwarn = new Alert(Alert.AlertType.WARNING);
        Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION);

        switch (alertType) {
            case 1: alert.setTitle("Error");
                alert.setHeaderText("Invalid ");
                alert.setContentText("Invalid customer input");
                alert.showAndWait();
                break;
            case 2: alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Confirmation ");
                alert.setContentText("Day is outside of business operation hours (Monday-Friday)");
                alert.showAndWait();
                break;
            case 3: alert.setTitle("Error");
                alert.setHeaderText("Error ");
                alert.setContentText("Customer ID entered is not valid.");
                alert.showAndWait();
                break;
            case 4: alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Confirmation ");
                alert.setContentText("Appointment has start time after end time");
                alert.showAndWait();
                break;
                case 5: alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Confirmation ");
                alert.setContentText("Appointment has same start and end time");
                alert.showAndWait();
                break;
            case 6: alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Confirmation ");
                alert.setContentText("Appointment overlaps with existing appointment.");
                alert.showAndWait();
                break;
            case 7: alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Confirmation ");
                alert.setContentText("Start time overlaps with existing appointment.");
                alert.showAndWait();
                break;
            case 8: alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Confirmation ");
                alert.setContentText("End time overlaps with existing appointment.");
                alert.showAndWait();
                break;

            case 9: alert.setTitle("ERROR");
                alert.setHeaderText("ERROR ");
                alert.setContentText("UserID entered is not valid.");
                alert.showAndWait();
                break;
            case 10: alert.setTitle("ERROR");
                alert.setHeaderText("ERROR ");
                alert.setContentText("Contact ID entered is not valid.");
                alert.showAndWait();
                break;

            case 11: alertwarn.setTitle("Warning");
                alert.setHeaderText("Warning ");
                alert.setContentText("Some User info is missing! Please fill out all fields!");
                alert.showAndWait();
                break;

        }}

    /**
     *  this cancels add appointment action and reverts the observable page view
     * @param event An Event representing some type of action
     * @throws IOException general exception that signals that an I/O exception of some sort has occurred
     */
    @FXML
    public void addAppointmentsCancel (ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("../views/appointments.fxml"));
        Scene scene = new Scene(root);
        Stage MainScreenReturn = (Stage)((Node)event.getSource()).getScene().getWindow();
        MainScreenReturn.setScene(scene);
        MainScreenReturn.show();
    }

    /**
     * This initializes the controls and fills the start and end time boxes by 15 minutes.
     * @throws SQLException An exception that provides information on a database access error or other errors
     */
    @FXML
    public void initialize() throws SQLException {

        ObservableList<Contacts> observableListContacts = contactAccess.getAllContacts();
        ObservableList<String> allContactsNames = FXCollections.observableArrayList();


        for (Contacts contacts : observableListContacts) {
            allContactsNames.add(contacts.getContactName());
        }

        ObservableList<String> appointmentTimes = FXCollections.observableArrayList();

        LocalTime firstAppointment = LocalTime.MIN.plusHours(8);
        LocalTime lastAppointment = LocalTime.MAX.minusHours(1).minusMinutes(45);


        if (!firstAppointment.equals(0) || !lastAppointment.equals(0)) {
            while (firstAppointment.isBefore(lastAppointment)) {
                appointmentTimes.add(String.valueOf(firstAppointment));
                firstAppointment = firstAppointment.plusMinutes(15);
            }
        }
        addAppointmentContact.setItems(allContactsNames);
        addAppointmentStartTime.setItems(appointmentTimes);
        addAppointmentEndTime.setItems(appointmentTimes);


    }

    /**
     * This method take values from the add-appointment view and saves it to the main appointment view as well as validates the values and displays a custom message if data is invalid.
     * @param event An Event representing some type of action
     * @throws IOException general exception that signals that an I/O exception of some sort has occurred
     */
    @FXML
    void addAppointmentSave(ActionEvent event) throws IOException {
try{

if(addAppointmentTitle.getText().isEmpty() || addAppointmentDescription.getText().isEmpty() || addAppointmentType.getText().isEmpty()
        || addAppointmentLocation.getText().isEmpty() || addAppointmentStartDate.getValue() == null || addAppointmentEndDate.getValue() == null ||
addAppointmentStartTime.getValue() == null || addAppointmentEndTime.getValue()== null || addAppointmentContact.getValue()== null){
    displayAlert(11);
    return;
}
        Connection connection = JDBC.startConnection();

        if (!addAppointmentTitle.getText().isEmpty() &&
                !addAppointmentDescription.getText().isEmpty() &&
                !addAppointmentLocation.getText().isEmpty() &&
                !addAppointmentType.getText().isEmpty() &&
                addAppointmentStartDate.getValue() != null &&
                addAppointmentEndDate.getValue() != null &&
                !addAppointmentStartTime.getValue().isEmpty() &&
                !addAppointmentEndTime.getValue().isEmpty())
        {

            ObservableList<Customers> getAllCustomers = customerAccess.getAllCustomers(connection);
            ObservableList<Integer> storeCustomerIDs = FXCollections.observableArrayList();
            ObservableList<userAccess> getAllUsers = userAccess.getAllUsers();
            ObservableList<Integer> storeUserIDs = FXCollections.observableArrayList();
            ObservableList<Appointments> getAllAppointments = appointmentAccess.getAllAppointments();


            for (Customers getAllCustomer : getAllCustomers) {
                Integer s = getAllCustomer.getCustomerID();
                storeCustomerIDs.add(s);
            }
            for (userAccess getAllUser : getAllUsers) {
                Integer t = getAllUser.getUserID();
                storeUserIDs.add(t);
            }
autoGenValue();

            LocalDate localDateStart = addAppointmentStartDate.getValue();
            LocalDate localDateEnd = addAppointmentEndDate.getValue();
            DateTimeFormatter minHourFormat = DateTimeFormatter.ofPattern("HH:mm");
            String appointmentStartDate = addAppointmentStartDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String appointmentStartTime = addAppointmentStartTime.getValue();

            String endDate = addAppointmentEndDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endTime = addAppointmentEndTime.getValue();

            System.out.println("thisDate + thisStart " + appointmentStartDate + " " + appointmentStartTime + ":00");
            ZonedDateTime startEstTime = convertTimeDateEST(appointmentStartDate + " " + appointmentStartTime + ":00");
            ZonedDateTime endEstTime = convertTimeDateEST(endDate + " " + endTime + ":00");

            int startAppointmentDayToCheckInt = startEstTime.getDayOfWeek().getValue();
            int endAppointmentDayToCheckInt = endEstTime.getDayOfWeek().getValue();

            int workWeekStart = DayOfWeek.MONDAY.getValue();
            int workWeekEnd = DayOfWeek.FRIDAY.getValue();

            LocalTime estBusinessStart = LocalTime.of(8, 0, 0);
            LocalTime estBusinessEnd = LocalTime.of(22, 0, 0);

            if (startAppointmentDayToCheckInt > workWeekEnd || startAppointmentDayToCheckInt < workWeekStart
                    ||endAppointmentDayToCheckInt > workWeekEnd || endAppointmentDayToCheckInt < workWeekStart ) {
               displayAlert(2);
                return;
            }
            LocalDateTime dateTimeStart = startEstTime.toLocalDateTime();
            LocalDateTime dateTimeEnd = endEstTime.toLocalDateTime();

            LocalDateTime lclStartTime = convertTimeDateLocal(startEstTime);
            LocalDateTime lclEndTime = convertTimeDateLocal(endEstTime);

            if (dateTimeStart.toLocalTime().isAfter(estBusinessEnd)|| dateTimeStart.toLocalTime().isBefore(estBusinessStart)
                    || dateTimeEnd.toLocalTime().isBefore(estBusinessStart) || dateTimeEnd.toLocalTime().isAfter(estBusinessEnd)) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Time is outside of business hours (8am-10pm EST): " + dateTimeStart.toLocalTime() +
                        " - " + dateTimeEnd.toLocalTime() + " EST");
                Optional<ButtonType> confirmation = alert.showAndWait();
                return;
            }


            int assignedAppointmentID = Integer.parseInt(String.valueOf((int) (Math.random() * 100)));
            int customerID = contactAccess.findCustomerID(addAppointmentCustomerID.getText());
            if (customerID <= 0) {
               displayAlert(3);
                return;
            }


            if (dateTimeStart.isAfter(dateTimeEnd)) {
                displayAlert(4);
                return;
            }


            if (dateTimeStart.isEqual(dateTimeEnd)) {
                displayAlert(5);
                return;
            }
            for (Appointments appointment : getAllAppointments) {
                LocalDateTime checkStart = appointment.getStart();
                LocalDateTime checkEnd = appointment.getEnd();


                if ((customerID == appointment.getCustomerID()) && (assignedAppointmentID != appointment.getAppointmentID()) &&
                        (lclStartTime.isBefore(checkStart)) && (lclEndTime.isAfter(checkEnd) ||
                                lclEndTime.isEqual(checkEnd))) {
                   displayAlert(6);
                    return;
                }

                if ((customerID == appointment.getCustomerID()) && (assignedAppointmentID != appointment.getAppointmentID()) &&
//
                        (lclStartTime.equals(checkStart) || (lclStartTime.isAfter(checkStart)) && (lclStartTime.isBefore(checkEnd)))) {
                    displayAlert(7);
                    return;
                }


                if (customerID == appointment.getCustomerID() && (assignedAppointmentID != appointment.getAppointmentID()) &&
                        (lclEndTime.equals(checkStart) || lclEndTime.equals(checkEnd) || (lclEndTime.isAfter(checkStart)) && (lclEndTime.isBefore(checkEnd)))) {
                    displayAlert(8);
                    return;
                }
            }

            String insertStatement = "INSERT INTO appointments " +
                    "(Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date," +
                    " Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES " +
                    "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            JDBC.setPreparedStatement(JDBC.getConnection(), insertStatement);
            PreparedStatement ps = JDBC.getPreparedStatement();
            ps.setInt(1, assignedAppointmentID);
            ps.setString(2, addAppointmentTitle.getText());
            ps.setString(3, addAppointmentDescription.getText());
            ps.setString(4, addAppointmentLocation.getText());
            ps.setString(5, addAppointmentType.getText());
            ps.setTimestamp(6, convertTimeDateUTC(startEstTime));
            ps.setTimestamp(7, convertTimeDateUTC(endEstTime));

            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(9, "admin");
            ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(11, 1);

            ps.setInt(12, customerID);
            int userId = contactAccess.findUserID(addAppointmentUserID.getText());

            if (userId <= 0) {
               displayAlert(9);
                return;
            }

            ps.setInt(13, userId);
            int contactId = contactAccess.findContactID(addAppointmentContact.getValue());
            if (contactId <= 0) {
               displayAlert(10);
                return;
            }
            ps.setInt(14, contactId);


            ps.execute();

        }

        Parent root = FXMLLoader.load(getClass().getResource("../views/appointments.fxml"));
        Scene scene = new Scene(root);
        Stage MainScreenReturn = (Stage) ((Node) event.getSource()).getScene().getWindow();
        MainScreenReturn.setScene(scene);
        MainScreenReturn.show();



} catch (
        SQLException e) {
    displayAlert(1);
}}

    /**
     * Generates a display text sense appointment ID is disabled
     */
    private void autoGenValue()
    {    if (addAppointmentID.getText().isBlank() || addAppointmentID.getText().isEmpty())
        addAppointmentID.setText("Auto Gen - Disabled");


    }

}

package controller;


import DataAccessObj.appointmentAccess;
import DataAccessObj.contactAccess;
import DataAccessObj.customerAccess;
import DataAccessObj.userAccess;
import Models.Appointments;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.JDBC;

import Models.Contacts;
import Models.Customers;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import static main.timeUtil.convertTimeDateEST;
import static main.timeUtil.convertTimeDateUTC;
import static main.timeUtil.convertTimeDateLocal;

/**
 * This class contains methods for validating data, controlling saves,loads,and deletes as well as sorting appointments
 */
 
public class appointmentsMain {
   

    @FXML
    private RadioButton allAppointmentRadio;
    @FXML
    private RadioButton appointmentWeekRadio;
    @FXML
    private RadioButton appointmentMonthRadio;
    @FXML
    private TableView<Appointments> allAppointmentsTable;
    @FXML
    private TableColumn<?, ?> appointmentContact;
    @FXML
    private TableColumn<?, ?> appointmentCustomerID;
    @FXML
    private TableColumn<?, ?> appointmentDescription;
    @FXML
    private TableColumn<?, ?> appointmentEnd;
    @FXML
    private TableColumn<?, ?> appointmentID;
    @FXML
    private TableColumn<?, ?> appointmentLocation;
    @FXML
    private TableColumn<?, ?> appointmentStart;
    @FXML
    private TableColumn<?, ?> appointmentTitle;
    @FXML
    private TableColumn<?, ?> appointmentType;
    @FXML
    private TableColumn<?, ?> tableContactID;
    @FXML
    private TableColumn<?, ?> tableUserID;
    @FXML
    private Button backToMainMenu;
    @FXML
    private Button deleteAppointment;
    @FXML
    private TextField updateAppointmentTitle;
    @FXML
    private TextField addAppointmentDescription;
    @FXML
    private TextField addAppointmentType;
    @FXML
    private TextField addAppointmentCustomerID;
    @FXML
    private TextField addAppointmentLocation;
    @FXML
    private TextField updateAppointmentID;
    @FXML
    private TextField addAppointmentUserID;
    @FXML
    private DatePicker addAppointmentStartDate;
    @FXML
    private DatePicker addAppointmentEndDate;
    @FXML
    private ComboBox<String> addAppointmentStartTime;
    @FXML
    private ComboBox<String> addAppointmentEndTime;
    @FXML
    private ComboBox<String> addAppointmentContact;
    @FXML
    private Button saveAppointment;

    /**
     *  method for displaying alerts
     * @param alertType displays different alert types and returns so user cant input invalid information
     */
    private void displayAlert(int alertType) {


        Alert alert = new Alert(Alert.AlertType.ERROR);

        Alert alertwarn = new Alert(Alert.AlertType.WARNING);
        Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        switch (alertType) {
            case 1:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid ");
                alert.setContentText("Invalid Customer ID");
                alert.showAndWait();
                break;
            case 2:
                alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Confirmation ");
                alert.setContentText("Day is outside of business operations (Monday-Friday)");
                alert.showAndWait();
                break;
            case 3:
                alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Confirmation ");
                alert.setContentText("Appointment has start time after end time");
                alert.showAndWait();
                break;
            case 4:
                alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Confirmation ");
                alert.setContentText("Appointment has same start and end time");
                alert.showAndWait();
                break;
            case 5:
                alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Confirmation ");
                alert.setContentText("Appointment overlaps with existing appointment.");
                alert.showAndWait();
                break;
            case 6:
                alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Confirmation ");
                alert.setContentText("Start time overlaps with existing appointment.");
                alert.showAndWait();
                break;
            case 7:
                alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Confirmation ");
                alert.setContentText("End time overlaps with existing appointment.");
                alert.showAndWait();
                break;

            case 8:
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR ");
                alert.setContentText("Customer ID is not valid.");
                alert.showAndWait();
                break;
            case 9:
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR ");
                alert.setContentText("User ID is not valid.");
                alert.showAndWait();
                break;
            case 10:
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR ");
                alert.setContentText("Contact ID is not valid.");
                alert.showAndWait();
                break;

            case 11:
                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR ");
                alert.setContentText("Some customer information is still blank! All fields must have a value before updating appointment!");
                alert.showAndWait();
                break;

        }
    }

    /**
     * This initializes the controls and sets observable list and variables.
     * @throws SQLException An exception that provides information on a database access error or other errors
     */
    public void initialize() throws SQLException {

        ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();

        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        appointmentDescription.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
        appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("appointmentLocation"));
        appointmentType.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        appointmentStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        tableContactID.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        tableUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));

        allAppointmentsTable.setItems(allAppointmentsList);
    }

    /**
     * This method just loads the add-appointments view when add is clicked.
     * @param event An Event representing some type of action
     * @throws IOException general exception that signals that an I/O exception of some sort has occurred
     */
    @FXML
    void addAppointment(ActionEvent event) throws IOException {

        Parent addParts = FXMLLoader.load(getClass().getResource("../views/addAppointments.fxml"));
        Scene scene = new Scene(addParts);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();

    }

    /**
     * This method just loads the main-screen menu view when back button is clicked.
     * @param event An Event representing some type of action
     * @throws IOException general exception that signals that an I/O exception of some sort has occurred
     */
    @FXML
    void backToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../views/MainScreen.fxml"));
        Scene scene = new Scene(root);
        Stage MainScreenReturn = (Stage) ((Node) event.getSource()).getScene().getWindow();
        MainScreenReturn.setScene(scene);
        MainScreenReturn.show();

    }

    /**
     *  this method delete's an appointment when clicking delete and ask for confirmation.
     * @param event An Event representing some type of action
     * @throws Exception general exception that signals that an I/O exception of some sort has occurred
     */
    @FXML
    void deleteAppointment(ActionEvent event) throws Exception {
        try {
            Connection connection = JDBC.startConnection();
            int deleteAppointmentID = allAppointmentsTable.getSelectionModel().getSelectedItem().getAppointmentID();
            String deleteAppointmentType = allAppointmentsTable.getSelectionModel().getSelectedItem().getAppointmentType();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the selected appointment with appointment id: " + deleteAppointmentID + " and appointment type " + deleteAppointmentType);
            Optional<ButtonType> confirmation = alert.showAndWait();
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                appointmentAccess.deleteAppointment(deleteAppointmentID, connection);

                ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();
                allAppointmentsTable.setItems(allAppointmentsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @lambda
     * This method loads an appointment, fills observable lists and checks appointment times.
     * Lambda #1: This lambda is used to add all of the contact names from the observable list with the proper contact information.
     */
    @FXML
    void loadAppointment() {
        try {
            JDBC.getConnection();
            Appointments selectedAppointment = allAppointmentsTable.getSelectionModel().getSelectedItem();

            if (selectedAppointment != null) {


                ObservableList<Contacts> observableListContacts = contactAccess.getAllContacts();
                ObservableList<String> allContactsNames = FXCollections.observableArrayList();
                String displayContactName = "";


                /**
                 * @lambda
                 * We can use a lambda here to load observable list in a "functional" manor for more readable and concise code.
                 * Lambdas are also useful in API support and parallel processing. Double colon operator can do the same thing.
                 */
                observableListContacts.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
                addAppointmentContact.setItems(allContactsNames);

                for (Contacts contact : observableListContacts
                ) {
                    if (selectedAppointment.getContactID() == contact.getId()) {
                        displayContactName = contact.getContactName();
                    }
                }

                updateAppointmentID.setText(String.valueOf(selectedAppointment.getAppointmentID()));
                updateAppointmentTitle.setText(selectedAppointment.getAppointmentTitle());
                addAppointmentDescription.setText(selectedAppointment.getAppointmentDescription());
                addAppointmentLocation.setText(selectedAppointment.getAppointmentLocation());
                addAppointmentType.setText(selectedAppointment.getAppointmentType());
                addAppointmentCustomerID.setText(String.valueOf(selectedAppointment.getCustomerID()));
                addAppointmentStartDate.setValue(selectedAppointment.getStart().toLocalDate());
                addAppointmentEndDate.setValue(selectedAppointment.getEnd().toLocalDate());
                addAppointmentStartTime.setValue(String.valueOf(selectedAppointment.getStart().toLocalTime()));
                addAppointmentEndTime.setValue(String.valueOf(selectedAppointment.getEnd().toLocalTime()));
                addAppointmentUserID.setText(String.valueOf(selectedAppointment.getUserID()));
                addAppointmentContact.setValue(displayContactName);

                ObservableList<String> appointmentTimes = FXCollections.observableArrayList();

                LocalTime firstAppointment = LocalTime.MIN.plusHours(8);
                LocalTime lastAppointment = LocalTime.MAX.minusHours(1).minusMinutes(45);


                if (!firstAppointment.equals(0) || !lastAppointment.equals(0)) {
                    while (firstAppointment.isBefore(lastAppointment)) {
                        appointmentTimes.add(String.valueOf(firstAppointment));
                        firstAppointment = firstAppointment.plusMinutes(15);
                    }
                }
                addAppointmentStartTime.setItems(appointmentTimes);
                addAppointmentEndTime.setItems(appointmentTimes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * This method is used to save an appointment when clicking update as well as setting the timezone and converting to est time with the help of timeUtil class.
     * This method also does time overlap checks, business hour checks, and other forms of validation before updating an appointment
     * @param event An Event representing some type of action
     */
    @FXML
    void saveAppointment(ActionEvent event) {
        try {
if(updateAppointmentTitle.getText().isEmpty()||addAppointmentDescription.getText().isEmpty() || addAppointmentLocation.getText().isEmpty() ||addAppointmentType.getText().isEmpty() ){
    displayAlert(11);
}
            Connection connection = JDBC.startConnection();

            if (!updateAppointmentTitle.getText().isEmpty() &&
                    !addAppointmentDescription.getText().isEmpty() &&
                    !addAppointmentLocation.getText().isEmpty() &&
                    !addAppointmentType.getText().isEmpty() &&
                    addAppointmentStartDate.getValue() != null &&
                    addAppointmentEndDate.getValue() != null &&
                    !addAppointmentStartTime.getValue().isEmpty() &&
                    !addAppointmentEndTime.getValue().isEmpty()) {
                ObservableList<Customers> getAllCustomers = customerAccess.getAllCustomers(connection);
                ObservableList<Integer> storeCustomerIDs = FXCollections.observableArrayList();
                ObservableList<userAccess> getAllUsers = userAccess.getAllUsers();
                ObservableList<Integer> storeUserIDs = FXCollections.observableArrayList();
                ObservableList<Appointments> getAllAppointments = appointmentAccess.getAllAppointments();



                String startDate = addAppointmentStartDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String startTime = addAppointmentStartTime.getValue();

                String endDate = addAppointmentEndDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String endTime = addAppointmentEndTime.getValue();

                ZonedDateTime convertStartEST = convertTimeDateEST(startDate + " " + startTime + ":00");
                ZonedDateTime convertEndEST = convertTimeDateEST(endDate + " " + endTime + ":00");

                LocalDateTime dateTimeStart = convertStartEST.toLocalDateTime();
                LocalDateTime dateTimeEnd = convertEndEST.toLocalDateTime();

                LocalDateTime lclDateTimeStart = convertTimeDateLocal(convertStartEST);
                LocalDateTime lclDateTimeEnd = convertTimeDateLocal(convertEndEST);



                Timestamp startUTC = convertTimeDateUTC(convertStartEST);
                Timestamp endUTC = convertTimeDateUTC(convertEndEST);



                if (convertStartEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue()) || convertStartEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue()) || convertEndEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue()) || convertEndEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue())) {
                    displayAlert(2);
                    return;
                }

                if (convertStartEST.toLocalTime().isBefore(LocalTime.of(8, 0, 0)) || convertStartEST.toLocalTime().isAfter(LocalTime.of(22, 0, 0)) || convertEndEST.toLocalTime().isBefore(LocalTime.of(8, 0, 0)) || convertEndEST.toLocalTime().isAfter(LocalTime.of(22, 0, 0))) {
                    System.out.println("time is outside of business hours");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Time is outside of business hours (8am-10pm EST): " + convertStartEST.toLocalTime() + " - " + convertEndEST.toLocalTime() + " EST");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }

                int newCustomerID = Integer.parseInt(addAppointmentCustomerID.getText());
                int appointmentID = Integer.parseInt(updateAppointmentID.getText());


                if (dateTimeStart.isAfter(dateTimeEnd)) {
                   displayAlert(3);
                    return;
                }

                if (dateTimeStart.isEqual(dateTimeEnd)) {
                   displayAlert(4);
                    return;
                }

                for (Appointments appointment : getAllAppointments) {
                    LocalDateTime checkStart = appointment.getStart();
                    LocalDateTime checkEnd = appointment.getEnd();


                    if ((newCustomerID == appointment.getCustomerID()) && (appointmentID != appointment.getAppointmentID()) &&
                            (lclDateTimeStart.isBefore(checkStart)) && (lclDateTimeEnd.isAfter(checkEnd) ||
                                            lclDateTimeEnd.isEqual(checkEnd))) {
                       displayAlert(5);
                        return;
                    }



                    if ((newCustomerID == appointment.getCustomerID()) && (appointmentID != appointment.getAppointmentID()) &&

                            (lclDateTimeStart.equals(checkStart) || (lclDateTimeStart.isAfter(checkStart)) && (lclDateTimeStart.isBefore(checkEnd)))) {
                        displayAlert(6);
                        return;
                    }


                    if (newCustomerID == appointment.getCustomerID() && (appointmentID != appointment.getAppointmentID()) &&

                            (lclDateTimeEnd.equals(checkStart) || lclDateTimeEnd.equals(checkEnd) || (lclDateTimeEnd.isAfter(checkStart)) && (lclDateTimeEnd.isBefore(checkEnd)))) {
                       displayAlert(7);
                        return;
                    }

                }





                String insertStatement = "UPDATE appointments SET Appointment_ID = ?, Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

                JDBC.setPreparedStatement(JDBC.getConnection(), insertStatement);
                PreparedStatement ps = JDBC.getPreparedStatement();


                int customerID = contactAccess.findCustomerID(addAppointmentCustomerID.getText());
                if (customerID <= 0) {
                displayAlert(8);
                    return;
                }

                if (addAppointmentCustomerID.getText().isEmpty() || addAppointmentCustomerID.getText().isBlank() || addAppointmentCustomerID.getText() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Customer ID is not really valid.");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }


                int userId = contactAccess.findUserID(addAppointmentUserID.getText());
                if (userId <= 0) {
                    displayAlert(9);
                    return;
                }
                int contactId = contactAccess.findContactID(addAppointmentContact.getValue());
                if (contactId <= 0) {
                   displayAlert(10);
                    return;
                }
                ps.setInt(1, Integer.parseInt(updateAppointmentID.getText()));
                ps.setString(2, updateAppointmentTitle.getText());
                ps.setString(3, addAppointmentDescription.getText());
                ps.setString(4, addAppointmentLocation.getText());
                ps.setString(5, addAppointmentType.getText());
                ps.setTimestamp(6, startUTC);
                ps.setTimestamp(7, endUTC);
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setInt(10, Integer.parseInt(addAppointmentCustomerID.getText()));
                ps.setInt(11, Integer.parseInt(addAppointmentUserID.getText()));
                ps.setInt(12, contactAccess.findContactID(addAppointmentContact.getValue()));
                ps.setInt(13, Integer.parseInt(updateAppointmentID.getText()));

                System.out.println("ps " + ps);
                ps.execute();

                ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();
                allAppointmentsTable.setItems(allAppointmentsList);
            }

        } catch (Exception e) {
            displayAlert(1);
            e.printStackTrace();
        }
        autoGenValue();
    }

    /**
     * This method sets ID text to auto gen due to it being disabled per requirement.
     */
    private void autoGenValue(){
        if(updateAppointmentID.getText().isEmpty() ||updateAppointmentID.getText().isBlank())
            updateAppointmentID.setText("Auto Gen - Disabled");}


    /**
     * This method sets observable list for when radio button allappointments is selected.
     * @param event An Event representing some type of action
     * @throws SQLException catches number format exception and appointment access .getAllAppointments
     */
    @FXML
    void appointmentAllSelected(ActionEvent event) throws SQLException {
        try {
            ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();

            if (allAppointmentsList != null)
                for (Models.Appointments appointment : allAppointmentsList) {
                    allAppointmentsTable.setItems(allAppointmentsList);
                }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method sets observable list for when radio button month appointments is selected.
     * @param event  an Event representing some type of action
     * @throws SQLException general exception for appointment access .getAllAppointments
     */
    @FXML
    void appointmentMonthSelected(ActionEvent event) throws SQLException {
        try {
            ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();
            ObservableList<Appointments> appointmentsMonth = FXCollections.observableArrayList();

            LocalDateTime currentMonthStart = LocalDateTime.now().minusMonths(1);
            LocalDateTime currentMonthEnd = LocalDateTime.now().plusMonths(1);


            if (allAppointmentsList != null)

            {
                for (Appointments appointment : allAppointmentsList) {
                    if (appointment.getEnd().isAfter(currentMonthStart) && appointment.getEnd().isBefore(currentMonthEnd)) {
                        appointmentsMonth.add(appointment);
                    }
                    allAppointmentsTable.setItems(appointmentsMonth);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method sets observable list for when radio button week appointments is selected.
     * @throws SQLException general exception for appointment access .getAllAppointments
     */
    @FXML
    void appointmentWeekSelected(ActionEvent event) throws SQLException {
        try {

            ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();
            ObservableList<Appointments> appointmentsWeek = FXCollections.observableArrayList();

            LocalDateTime weekStart = LocalDateTime.now().minusWeeks(1);
            LocalDateTime weekEnd = LocalDateTime.now().plusWeeks(1);

            if (allAppointmentsList != null)

            {
                for (Appointments appointment : allAppointmentsList) {
                    if (appointment.getEnd().isAfter(weekStart) && appointment.getEnd().isBefore(weekEnd)) {
                        appointmentsWeek.add(appointment);
                    }
                    allAppointmentsTable.setItems(appointmentsWeek);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package controller;
import DataAccessObj.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.JDBC;
import Models.*;
import utils.CountriesConverter;
import utils.FirstLevelDivisionConverter;

import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * This class implements adding deleting and updating customer data as well as checks for validation/confirmation and overall control of customer data.
 */
public class CustomerController implements Initializable {
    @FXML private TableColumn<?, ?> customerRecordsTableName;
    @FXML private Button customerRecordsAddCustomer;
    @FXML private Button customerRecordsCancel;
    @FXML private TableView<Customers> customerRecordsTable;
    @FXML private TableColumn<?, ?> customerRecordsTableAddress;
    @FXML private TableColumn<?, ?> customerRecordsTableID;
    @FXML private TableColumn<?, ?> customerRecordsTablePhone;
    @FXML private TableColumn<?, ?> customerRecordsTablePostalCode;
    @FXML private TableColumn<?, ?> customerRecordsTableState;
    @FXML private TableColumn<?, ?> customerRecordsTableCountry;
    @FXML private TextField customerIDEdit;
    @FXML private TextField customerNameEdit;
    @FXML private TextField customerEditPhone;
    @FXML private TextField customerEditPostal;
    @FXML private ComboBox<firstLevelDivisionAccess> customerEditState;
    @FXML private ComboBox<CountryAccess> customerEditCountry;
    @FXML private TextField customerAddressEdit;

    /**
     * method for displaying alerts
     * @param alertType displays different alert types and returns so user cant input invalid information
     */
    private void displayAlert(int alertType) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        Alert alertwarn = new Alert(Alert.AlertType.WARNING);
        Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        switch (alertType) {
            case 1: alert.setTitle("Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Must fill appropriate boxes");
                alert.showAndWait();
                break;
          case 2: alert.setTitle("Error");
        alert.setHeaderText("Invalid Input");
        alert.setContentText("Must Click (Edit Customer) First, Then fill appropriote fields. ");
        alert.showAndWait();

            case 3:  alert.setTitle("Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Clicking (save) is for edits only.");
                alert.showAndWait();
        break;
            case 4: alert.setTitle("Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Must delete appointments before deleting customer.");
                alert.showAndWait();
                break;

            case 5: alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Delete Confirmation");
                alert.setContentText("Customer has been deleted.");
                alert.showAndWait();
                break;
            case 6: alertConfirm.setTitle("Confirmation");
                alert.setHeaderText("Delete Confirmation");
                alert.setContentText("select a customer first.");
                alert.showAndWait();
                break;
            case 7: alert.setTitle("ERROR");
                alert.setHeaderText("ERROR");
                alert.setContentText("Some customer information is missing.");
                alert.showAndWait();
                break;
            case 8: alert.setTitle("ERROR");
                alert.setHeaderText("ERROR");
                alert.setContentText("DB connection issue.");
                alert.showAndWait();
                break;
            case 9: alert.setTitle("ERROR");
                alert.setHeaderText("ERROR");
                alert.setContentText("Please select the state/ province.");
                alert.showAndWait();
                break;

    }}


    /**
     * This method just loads main screen view when cancel button is clicked.
     * @throws IOException general exception that signals that an I/O exception of some sort has occurred
     */

    @FXML
    public void customerRecordsCancel(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("../views/MainScreen.fxml"));
        Scene scene = new Scene(root);
        Stage MainScreenReturn = (Stage) ((Node) event.getSource()).getScene().getWindow();
        MainScreenReturn.setScene(scene);
        MainScreenReturn.show();
    }

    /**
     * Controller initialization interface. Called to initialize a controller after its root element has been completely processed
     * Used to set initial variables and list as well as initialize controls and first level division converter .
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Connection connection = JDBC.startConnection();

            ObservableList<CountryAccess> allCountries = CountryAccess.getCountries();
            ObservableList<Country> countryNames = FXCollections.observableArrayList();
            ObservableList<firstLevelDivisionAccess> allFirstLevelDivisions = firstLevelDivisionAccess.getAllFirstLevelDivisions();
            ObservableList<firstLevelDivision> firstLevelDivisionAllNames = FXCollections.observableArrayList();
            ObservableList<Customers> allCustomersList = customerAccess.getAllCustomers(connection);

            customerRecordsTableID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            customerRecordsTableName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            customerRecordsTableAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
            customerRecordsTablePostalCode.setCellValueFactory(new PropertyValueFactory<>("customerPostalCode"));
            customerRecordsTablePhone.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
            customerRecordsTableState.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

            customerEditCountry.getItems().addAll(allCountries.stream().toList());
            customerEditCountry.setConverter(new CountriesConverter());

            customerEditState.getItems().addAll(allFirstLevelDivisions.stream().toList());
            customerEditState.setConverter(new FirstLevelDivisionConverter());

            customerRecordsTable.setItems(allCustomersList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to delete customers and refresh the list as well as check if customer has appointments already and if not to return.
     * This method also gives user confirmation that a customer has been deleted if delete was considered a valid delete
     * @throws Exception general sql exception .getAllAppointments
     * @param event representation of the action when a button is pressed.
     */
    @FXML
    void customerRecordsDelete(ActionEvent event) throws Exception {
try{
        Connection connection = JDBC.startConnection();
        ObservableList<Appointments> getAllAppointmentsList = appointmentAccess.getAllAppointments();


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the selected customer? ");
        Optional<ButtonType> confirmation = alert.showAndWait();
        if (customerRecordsTable.getSelectionModel().getSelectedItem() == null) {
            displayAlert(6);
            return;
        }

        if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {

            int deleteCustomerID = customerRecordsTable.getSelectionModel().getSelectedItem().getCustomerID();
            appointmentAccess.deleteAppointment(deleteCustomerID, connection);

            String sqlDelete = "DELETE FROM customers WHERE Customer_ID = ?";
            JDBC.setPreparedStatement(JDBC.getConnection(), sqlDelete);

            PreparedStatement psDelete = JDBC.getPreparedStatement();
            int customerFromTable = customerRecordsTable.getSelectionModel().getSelectedItem().getCustomerID();


            for (Appointments appointment: getAllAppointmentsList) {
                int customerFromAppointments = appointment.getCustomerID();
                if (customerFromTable == customerFromAppointments) {
                    String deleteStatementAppointments = "DELETE FROM appointments WHERE Appointment_ID = ?";
                    JDBC.setPreparedStatement(JDBC.getConnection(), deleteStatementAppointments);
                }

            }
            psDelete.setInt(1, customerFromTable);

            psDelete.execute();
            ObservableList<Customers> refreshCustomersList = customerAccess.getAllCustomers(connection);
            customerRecordsTable.setItems(refreshCustomersList);
            displayAlert(5);
        }
} catch (SQLException e) {
    e.printStackTrace();
displayAlert(4);
    }}

    /**
     * This method loads the selected customer when edit button is clicked.
     * validation check for clicking save when not in edit
     * validation for first level division access and sets values
     * @throws SQLException general sql exception for improper button press when call to database
     * @param event representation of the action when a button is pressed.
     */


    @FXML
    void customerRecordsEditCustomerButton(ActionEvent event)  throws SQLException {
        try {
            JDBC.startConnection();
            Customers selectedCustomer = (Customers) customerRecordsTable.getSelectionModel().getSelectedItem();

            String divisionName = "", countryName = "";

            if (selectedCustomer != null) {
                ObservableList<CountryAccess> getAllCountries = CountryAccess.getCountries();
                ObservableList<firstLevelDivisionAccess> getFLDivisionNames = firstLevelDivisionAccess.getAllFirstLevelDivisions();
                ObservableList<String> allFLDivision = FXCollections.observableArrayList();
                getFLDivisionNames.stream().map(firstLevelDivisionAccess::getDivisionName)
                        .collect(Collectors.toList());
                customerEditState.setItems(getFLDivisionNames);
                customerEditCountry.setItems(getAllCountries);
                customerIDEdit.setText(String.valueOf((selectedCustomer.getCustomerID())));
                customerNameEdit.setText(selectedCustomer.getCustomerName());
                customerAddressEdit.setText(selectedCustomer.getCustomerAddress());
                customerEditPostal.setText(selectedCustomer.getCustomerPostalCode());
                customerEditPhone.setText(selectedCustomer.getCustomerPhone());
                Optional<firstLevelDivisionAccess> optFlDivision = getFLDivisionNames.stream().filter(divi -> divi.getDivisionID()== selectedCustomer.getCustomerDivisionID()).findFirst();
                if (optFlDivision.isPresent()) {
                    firstLevelDivisionAccess selectedObje = optFlDivision.get();
                    Optional<CountryAccess> optCountry = getAllCountries.stream().filter(cont ->
                            cont.getCountryID() == selectedObje.getCountry_ID()).findFirst();
                    if (optCountry.isPresent()) {
                        customerEditCountry.setValue(optCountry.get());
                        customerEditCountryDropDown(getFLDivisionNames, optCountry.get().getCountryName());
                    }
                    customerEditState.setValue(selectedObje);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            displayAlert(3);
        }
    }

    /**
     *  This method is for adding a new customer and refreshing observable list
     *  validation for leaving empty boxes and state/province null
     * @param event representation of the action when a button is pressed.
     */
    @FXML
    void customerRecordsAddCustomer(ActionEvent event)  {
        try {
            if (customerEditState == null || customerEditState.getSelectionModel() == null || customerEditState.getSelectionModel().getSelectedItem() == null) {
                displayAlert(9);
                return;
            }
            if(customerNameEdit.getText().isEmpty()|| customerAddressEdit.getText().isEmpty()|| customerEditPhone.getText().isEmpty()|| customerEditPostal.getText().isEmpty()){
                displayAlert(1);
                return;
            }

            Connection connection = JDBC.startConnection();
            if (!customerNameEdit.getText().isEmpty() || !customerAddressEdit.getText().isEmpty() ||
                    customerEditCountry.getValue() != null || !customerEditPostal.getText().isEmpty() ||
                    !customerEditPhone.getText().isEmpty() ||
                    customerEditState.getValue() != null )
            {


                Integer newCustomerID = (int) (Math.random() * 100);

                String insertStatement = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?,?)";
                JDBC.setPreparedStatement(JDBC.getConnection(), insertStatement);
                PreparedStatement ps = JDBC.getPreparedStatement();
                ps.setInt(1, newCustomerID);
                ps.setString(2, customerNameEdit.getText());
                ps.setString(3, customerAddressEdit.getText());
                ps.setString(4, customerEditPostal.getText());
                ps.setString(5, customerEditPhone.getText());
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(7, "admin");
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setInt(10, customerEditState.getSelectionModel().getSelectedItem().getDivisionID());
                ps.execute();

                customerIDEdit.clear();
                customerNameEdit.clear();
                customerAddressEdit.clear();
                customerEditPostal.clear();
                customerEditPhone.clear();

                ObservableList<Customers> refreshCustomersList = customerAccess.getAllCustomers(connection);
                customerRecordsTable.setItems(refreshCustomersList);

            } else {
                displayAlert(7);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            displayAlert(8);
            return;
        }
        autoGenValue();
    }

    /**
     * This method is to add customer when the save button is clicked.
     * validation check for improper add button click,empty/null boxes and state/province being null.
     * @param event representation of the action when a button is pressed.
     */
    @FXML
    void customerRecordsSaveCustomer(ActionEvent event)  {
        try {
            Connection connection = JDBC.startConnection();
            if (customerNameEdit.getText().isEmpty() || customerAddressEdit.getText().isEmpty() || customerAddressEdit.getText().isEmpty()
                    || customerEditPostal.getText().isEmpty() || customerEditPhone.getText().isEmpty() ||
                    customerEditCountry.getValue() == null || customerEditState.getValue() == null){displayAlert(1);
            return;}
            {
                if (customerEditState == null || customerEditState.getSelectionModel() == null || customerEditState.getSelectionModel().getSelectedItem() == null) {
                    displayAlert(9);
                    return;
                }
                customerEditState.getSelectionModel().getSelectedIndex();


                String insertStatement = "UPDATE customers SET Customer_ID = ?, Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, " +
                        "Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
                JDBC.setPreparedStatement(JDBC.getConnection(), insertStatement);
                PreparedStatement ps = JDBC.getPreparedStatement();
                ps.setInt(1, Integer.parseInt(customerIDEdit.getText()));
                ps.setString(2, customerNameEdit.getText());
                ps.setString(3, customerAddressEdit.getText());
                ps.setString(4, customerEditPostal.getText());
                ps.setString(5, customerEditPhone.getText());
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(7, "admin");
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setInt(10, customerEditState.getSelectionModel().getSelectedItem().getDivisionID());
                ps.setInt(11, Integer.parseInt(customerIDEdit.getText()));
                ps.execute();

                customerIDEdit.clear();
                customerNameEdit.clear();
                customerAddressEdit.clear();
                customerEditPostal.clear();
                customerEditPhone.clear();

                ObservableList<Customers> refreshCustomersList = customerAccess.getAllCustomers(connection);
                customerRecordsTable.setItems(refreshCustomersList);

            }
        } catch (Exception e) {
            e.printStackTrace();
displayAlert(3);
        }

    }

    /**
     * This method sets observable lists to add for each corresponding country for the drop down list.
     * @param getAllFirstLevelDivisions collects all first level divisions
     * @param selectedCountry controls country selection
     * @lambda
     * lambda#2: Instead of creating a separate method I can create this lambda in the body of this method where it makes sense.
     * taking the parameters of the first level division and assigning them to there corresponding country value makes it more scalable and readable.
     */
    private void customerEditCountryDropDown(ObservableList<firstLevelDivisionAccess> getAllFirstLevelDivisions, String selectedCountry) {
        ObservableList<firstLevelDivisionAccess> flDivisionUS = FXCollections.observableArrayList();
        ObservableList<firstLevelDivisionAccess> flDivisionUK = FXCollections.observableArrayList();
        ObservableList<firstLevelDivisionAccess> flDivisionCanada = FXCollections.observableArrayList();
/**
 * @lambda #2: Parameters of the first level division country ID and assigning them to there corresponding country value.
 * Calling interfaces method to run the expression. This makes the code more readable and scalable.
 * However lambda's also support API design/implementation and enable parallel processing.
  */

        getAllFirstLevelDivisions.forEach(firstLevelDivision -> {
            if (firstLevelDivision.getCountry_ID() == 1) {
                flDivisionUS.add(firstLevelDivision);
            } else if (firstLevelDivision.getCountry_ID() == 2) {
                flDivisionUK.add(firstLevelDivision);
            } else if (firstLevelDivision.getCountry_ID() == 3) {
                flDivisionCanada.add(firstLevelDivision);
            }
        });

        if (selectedCountry.equals("U.S")) {
            customerEditState.setItems(flDivisionUS);
        }
        else if (selectedCountry.equals("UK")) {
            customerEditState.setItems(flDivisionUK);
        }
        else if (selectedCountry.equals("Canada")) {
            customerEditState.setItems(flDivisionCanada);
        }

    }

    /**
     * This method sets the text for customer ID to auto gen and is disabled in the fxml due to project requirements.
     */
    private void autoGenValue()
    {    if (customerIDEdit.getText().isBlank() || customerIDEdit.getText().isEmpty())
        customerIDEdit.setText("Auto Gen - Disabled");


    }

    /**
     *
     * This method loads drop-down menu with first level division data on button clicked and sets division access for each country.
     * @param event representation of the action when a button is pressed.
     * @throws SQLException general sql exception for .getALLFirstLevelDivision
     * @lambda
     *  lambda#2: Instead of creating a separate method I can create this lambda in the body of this method where it makes sense.
     *  taking the parameters of the first level division and assigning them to there corresponding country value makes it more scalable and readable.
     */
    public void customerEditCountryDropDown(ActionEvent event) throws SQLException {
        try {
            if (customerEditCountry.getSelectionModel().getSelectedItem() == null)
                return;
            String selectedCountry = customerEditCountry.getSelectionModel().getSelectedItem().getCountryName();
            JDBC.startConnection();
            ObservableList<firstLevelDivisionAccess> getAllFirstLevelDivisions = firstLevelDivisionAccess.getAllFirstLevelDivisions();

            ObservableList<firstLevelDivisionAccess> flDivisionUS = FXCollections.observableArrayList();
            ObservableList<firstLevelDivisionAccess> flDivisionUK = FXCollections.observableArrayList();
            ObservableList<firstLevelDivisionAccess> flDivisionCanada = FXCollections.observableArrayList();
/**
 * @lambda #2: Parameters of the first level division country ID and assigning them to there corresponding country value.
 * Calling interfaces method to run the expression. This makes the code more readable and scalable.
 * However lambda's also support API design/implementation and enable parallel processing.
 */
            getAllFirstLevelDivisions.forEach(firstLevelDivision -> {
                if (firstLevelDivision.getCountry_ID() == 1) {
                    flDivisionUS.add(firstLevelDivision);
                } else if (firstLevelDivision.getCountry_ID() == 2) {
                    flDivisionUK.add(firstLevelDivision);
                } else if (firstLevelDivision.getCountry_ID() == 3) {
                    flDivisionCanada.add(firstLevelDivision);
                }
            });


            if (selectedCountry.equals("U.S")) {
                customerEditState.setItems(flDivisionUS);
            }
            else if (selectedCountry.equals("UK")) {
                customerEditState.setItems(flDivisionUK);
            }
            else if (selectedCountry.equals("Canada")) {
                customerEditState.setItems(flDivisionCanada);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }}

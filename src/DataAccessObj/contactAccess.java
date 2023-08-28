package DataAccessObj;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.JDBC;
import Models.Contacts;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class contactAccess {
    public static ObservableList<Contacts> getAllContacts() throws SQLException {
        ObservableList<Contacts> observableListContacts
                = FXCollections.observableArrayList();
        String sql = "SELECT * from contacts";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int contactID = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");
            String contactEmail = rs.getString("Email");
            Contacts contact = new Contacts(contactID, contactName, contactEmail);
            observableListContacts
                    .add(contact);
        }
        return observableListContacts
                ;
    }

    /**
     * Find contact ID given contact name.
     * @throws SQLException
     * @param contactName
     * @return contactID
     */
    public static int findContactID(String contactName) throws SQLException {
        PreparedStatement ps = JDBC.getConnection().prepareStatement("SELECT * FROM contacts WHERE Contact_Name = ? OR contact_id = ?");
        ps.setString(1, contactName);
        ps.setString(2, contactName);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("Contact_ID");
        }
        return -1;
    }

    public static int findUserID(String userName) throws SQLException {
        PreparedStatement ps = JDBC.getConnection().prepareStatement("SELECT * FROM users WHERE user_Name = ? OR user_id = ?");
        ps.setString(1, userName);
        ps.setString(2, userName);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("user_ID");
        }
        return -1;
    }

    public static int findCustomerID(String customerName) throws SQLException {
        PreparedStatement ps = JDBC.getConnection().prepareStatement("SELECT * FROM customers WHERE Customer_Name = ? OR customer_id = ?");
        ps.setString(1, customerName);
        ps.setString(2, customerName);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("customer_ID");
        }
        return -1;
    }
}

package DataAccessObj;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.JDBC;
import Models.Country;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class CountryAccess extends Country {

    public CountryAccess(int countryID, String countryName) {
        super(countryID, countryName);
    }

    /**
     * ObservableList that queries Country_ID and Country from the countries database table.
     * @throws SQLException
     * @return countriesObservableList
     */
    public static ObservableList<CountryAccess> getCountries() throws SQLException {
        ObservableList<CountryAccess> countriesObservableList = FXCollections.observableArrayList();
        String sql = "SELECT Country_ID, Country from countries";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int countryID = rs.getInt("Country_ID");
            String countryName = rs.getString("Country");
            CountryAccess country = new CountryAccess(countryID, countryName);
            countriesObservableList.add(country);
        }
        return countriesObservableList;
    }

}

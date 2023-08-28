package utils;

import DataAccessObj.CountryAccess;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;

public class CountriesConverter  extends StringConverter<CountryAccess> {
    private Map<String, CountryAccess> countryMap = new HashMap<>();

    @Override
    public String toString(CountryAccess countryAccess) {
        if (countryAccess == null)
            return "Please select state";
        countryMap.put(countryAccess.getCountryName(), countryAccess);
        return countryAccess.getCountryName();
    }

    @Override
    public CountryAccess fromString(String name) {
        return countryMap.get(name);
    }

}

package utils;

import DataAccessObj.CountryAccess;
import DataAccessObj.firstLevelDivisionAccess;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;

public class FirstLevelDivisionConverter extends StringConverter<firstLevelDivisionAccess> {
    private Map<String, firstLevelDivisionAccess> firstLevelDivisionAccessMap = new HashMap<>();

    @Override
    public String toString(firstLevelDivisionAccess firstLevelDivisionAccess) {
        if (firstLevelDivisionAccess == null)
            return "Please select state";
        firstLevelDivisionAccessMap.put(firstLevelDivisionAccess.getDivisionName(), firstLevelDivisionAccess);
        return firstLevelDivisionAccess.getDivisionName();
    }

    @Override
    public firstLevelDivisionAccess fromString(String name) {
        return firstLevelDivisionAccessMap.get(name);
    }

}

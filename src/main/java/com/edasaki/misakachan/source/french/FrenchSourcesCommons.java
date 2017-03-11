package com.edasaki.misakachan.source.french;

import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.source.SearchAction;
import com.edasaki.misakachan.source.SearchResult;
import com.edasaki.misakachan.source.SearchResultSet;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by farich on 10/03/17.
 */
public class FrenchSourcesCommons {

    private FrenchSourcesCommons()
    {

    }

    public static Integer tryParseInt(String stringNumber) {
        Integer retVal;
        try {
            retVal = Integer.parseInt(stringNumber);
            return retVal;
        } catch (NumberFormatException nfe) {

            try {
                String[] numbers = stringNumber.replaceAll("[^0-9]+", " ").trim().split(" ");
                return numbers.length > 0 ? Integer.parseInt(numbers[0]) : 0;
            } catch (Exception e) {
                return 0;
            }
        }
    }


        public static SearchAction buildSearchAction(Function<String, Collection<SearchResult>> searchFunction , AbstractSource myAbstractSource) {
            return searchString -> {
                Collection<SearchResult> results = searchFunction.apply(searchString);
                SearchResultSet searchResultSet = new SearchResultSet(myAbstractSource);
                searchResultSet.addResults(results);
                return searchResultSet;
            };

        }

    static boolean stringMatch(String first, String sec) {
        if (StringUtils.isEmpty(first) && !StringUtils.isEmpty(sec)) return true;
        if (StringUtils.containsIgnoreCase(first, sec)) return true;
        if (StringUtils.containsIgnoreCase(sec, first)) return true;

        return false;

    }




}

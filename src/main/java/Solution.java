import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class Solution {

    private static final Pattern NUMBER_OF_PAGES_PATTERN = compile("\"total_pages\":[0-9]*");
    private static final Pattern POPULATIONS_PATTERN = compile("\"population\":[0-9]*");
    private static final int FIRST_PAGE = 1;
    private static final int SECOND_PAGE = 2;
    private static final int INDEX_WITH_VALUE = 1;
    private static final String SEPARATOR = ":";
    private static final int DEFAULT_NUMBER_OF_PAGES = 1;
    private static final String URL_ROOT = "https://jsonmock.hackerrank.com/api/countries/search?name=";
    private static final String URL_PAGE_PARAM = "&page=";
    private static final String HTTP_GET = "GET";

    public static int getCountries(String substringCountry, int minValuePopulation) {

        String firstPageText = getPageText(substringCountry, FIRST_PAGE);
        int numberOfPages = getNumberOfPages(firstPageText);
        int numberOfPopulation = getPopulationFrom(firstPageText, minValuePopulation);

        for (int i = SECOND_PAGE; i <= numberOfPages; i++) {
            String nextPageText = getPageText(substringCountry, i);
            numberOfPopulation += getPopulationFrom(nextPageText, minValuePopulation);
        }

        return numberOfPopulation;
    }

    private static int getPopulationFrom(String pageText, int minPopulationValue) {
        Matcher matcher = POPULATIONS_PATTERN.matcher(pageText);
        int populationNumber = 0;

        while (matcher.find()) {
            String populationPropertiesValue = matcher.group();
            String[] propertiesValue = populationPropertiesValue.split(SEPARATOR);
            int populationValue = Integer.parseInt(propertiesValue[INDEX_WITH_VALUE]);

            if (populationValue > minPopulationValue) {
                populationNumber++;
            }
        }

        return populationNumber;
    }

    private static int getNumberOfPages(String firstPageText) {
        Matcher matcher = NUMBER_OF_PAGES_PATTERN.matcher(firstPageText);

        if (matcher.find()) {
            String numberOfPages = matcher.group();
            String[] propertiesValue = numberOfPages.split(SEPARATOR);
            return Integer.parseInt(propertiesValue[INDEX_WITH_VALUE]);
        }

        return DEFAULT_NUMBER_OF_PAGES;
    }

    private static String getPageText(String countrySubstringName, int page) {
        HttpURLConnection connection = null;
        String textFromSinglePage = "";

        try {
            URL url = new URL(URL_ROOT + countrySubstringName + URL_PAGE_PARAM + page);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(HTTP_GET);
            textFromSinglePage = getTextFromSinglePage(connection);
        } catch (IOException e) {
            System.out.println("Can't connect!");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return textFromSinglePage;
    }

    private static String getTextFromSinglePage(HttpURLConnection connection) throws IOException {
        String line;
        StringBuilder text = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
            }
        }

        return text.toString();
    }
}

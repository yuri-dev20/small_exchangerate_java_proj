import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Scanner;

class CurrencyConnection {
    private String url;
    Hashtable<String, Double> hashtable = new Hashtable<>();

    public CurrencyConnection(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    JsonObject getCurrencyJson() {
        try {
            URL url = new URL(getUrl());
            // Making Request

            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            if (request.getResponseCode() != 200) {
                throw new RuntimeException("HttpResponseCode: " + request.getResponseCode());

            } else {
                JsonParser jp = new JsonParser();
                JsonElement je = jp.parse(new InputStreamReader((InputStream) request.getContent()));

                JsonObject jObj = je.getAsJsonObject();
                // TODO: Map the conversion values in a hash tables
                // TODO: Make options to choose a value to convert from BRL

                return jObj.get("conversion_rates").getAsJsonObject();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    void initializeValues() {
        hashtable.put("PHP", getCurrencyJson().get("PHP").getAsDouble());
        hashtable.put("BRL", getCurrencyJson().get("BRL").getAsDouble());
        hashtable.put("USD", getCurrencyJson().get("USD").getAsDouble());
        hashtable.put("JPY", getCurrencyJson().get("JPY").getAsDouble());
        hashtable.put("KRW", getCurrencyJson().get("KRW").getAsDouble());
        hashtable.put("EUR", getCurrencyJson().get("EUR").getAsDouble());
    }

    void showValues(String exchange, Double exchangeValue) {
        initializeValues();
        System.out.println("Converting");

        hashtable.remove(exchange);

        hashtable.forEach((key, value) -> System.out.println(key + ": " + exchangeValue / value));
    }

}

public class Main {
    public static void main(String[] args) {
        // https://www.exchangerate-api.com get the API here for free
        /* String api = YOUR API KEY; */

        Scanner sc = new Scanner(System.in);

        Hashtable<String, String> hashcountry = new Hashtable<>() {{
           put("Philippine", "PHP");
           put("Brasil", "BRL");
           put("US Dollar", "USD");
           put("Japanese Yen", "JPY");
           put("South Korean Won", "KRW");
           put("Euro", "EUR");
        }};

        System.out.println("Choose an country: ");

        hashcountry.forEach((key, value) -> System.out.println(key + ": " + value));

        System.out.println();
        System.out.println("Choose an option: ");
        String opt = sc.nextLine();

        for (String country : hashcountry.values()) {
            if (Objects.equals(opt, country)) {
                String url_str = "https://v6.exchangerate-api.com/v6/" + api + "/latest/" + opt.toUpperCase();
                CurrencyConnection conn = new CurrencyConnection(url_str);
                System.out.print("Enter a value for exchange: ");
                Double userValue = sc.nextDouble();
                System.out.println();
                conn.showValues(opt.toUpperCase(), userValue);

            }
        }

        sc.close();
    }
}

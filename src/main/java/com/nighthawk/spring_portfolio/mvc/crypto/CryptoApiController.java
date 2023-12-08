package com.nighthawk.spring_portfolio.mvc.crypto;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crypto")
@CrossOrigin(origins = {"http://127.0.0.1:4000"})
public class CryptoApiController {
    private Object body;
    private HttpStatus status;
    private String last_run = null;

    protected JSONArray getCryptoMarketDataFromAPI(String symbolId) throws Exception {
        String today = new Date().toString().substring(0, 10);
        ZonedDateTime oneWeekAgo = ZonedDateTime.now(ZoneOffset.UTC).minus(Duration.ofDays(7));
        String startTime = oneWeekAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        int limit = 20;

        String apiUrl = String.format("https://rest.coinapi.io/v1/trades/%s/history?time_start=%s&limit=%d", symbolId, startTime, limit);

        if (last_run == null || !today.equals(last_run)) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("x-coinapi-key", "A45C5875-F234-49DA-BED1-E30E1E15EA9E")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            Object parsedResponse = new JSONParser().parse(response.body());

            if (parsedResponse instanceof JSONArray) {
                return (JSONArray) parsedResponse;
            } else {
                throw new Exception("Invalid response format");
            }
        }
        return new JSONArray();
    }

    protected List<Transaction> convertToTransactionList(JSONArray cryptoAPIData) {
        List<Transaction> transactions = new ArrayList<>();
        for (Object obj : cryptoAPIData) {
            JSONObject transactionData = (JSONObject) obj;
            double size = Double.parseDouble(transactionData.get("size").toString());
            Transaction transaction = new Transaction(size);
            transactions.add(transaction);
        }
        return transactions;
    }
    //hi
    
    // Transaction class representing individual transactions
    public static class Transaction {
        private double size;

        public Transaction(double size) {
            this.size = size;
        }

        public double getSize() {
            return size;
        }
        // Add other attributes and methods as needed
    }
}

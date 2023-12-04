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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crypto")
public class CryptoApiController {
    private Object body;
    private HttpStatus status;
    private String last_run = null;

    @GetMapping("/market/{symbolId}")
    public ResponseEntity<Object> getCryptoMarketData(@PathVariable String symbolId) {
        try {
            JSONArray cryptoAPIData = getCryptoMarketDataFromAPI(symbolId);

            List<Transaction> transactions = convertToTransactionList(cryptoAPIData);

            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            JSONObject errorBody = new JSONObject();
            errorBody.put("status", "Failed to fetch or sort transactions: " + e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/market/{symbolId}/selection")
public ResponseEntity<Object> sortBySelection(@PathVariable String symbolId) {
    try {
        long startTime = System.currentTimeMillis();

        JSONArray cryptoAPIData = getCryptoMarketDataFromAPI(symbolId);

        List<Transaction> transactions = convertToTransactionList(cryptoAPIData);

        AtomicInteger comparisons = new AtomicInteger(0);
        AtomicInteger swaps = new AtomicInteger(0);

        selectionSortTransactionsBySize(transactions, comparisons, swaps);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        String successMessage = "Transactions sorted successfully by size. " +
                "Comparisons: " + comparisons.get() + ", Swaps: " + swaps.get() +
                ". Execution time: " + executionTime + " milliseconds";

        Map<String, Object> response = new HashMap<>();
        response.put("message", successMessage);
        response.put("sortedTransactions", transactions);

        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        JSONObject errorBody = new JSONObject();
        errorBody.put("status", "Failed to fetch or sort transactions: " + e.getMessage());
        return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}



    @GetMapping("/market/{symbolId}/bubble")

        public ResponseEntity<Object> sortByBubble(@PathVariable String symbolId) {
        try {
            long startTime = System.currentTimeMillis();

            JSONArray cryptoAPIData = getCryptoMarketDataFromAPI(symbolId);

            List<Transaction> transactions = convertToTransactionList(cryptoAPIData);

            AtomicInteger comparisons = new AtomicInteger(0);
            AtomicInteger swaps = new AtomicInteger(0);

            bubbleSortTransactionsBySize(transactions, comparisons, swaps);

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            String successMessage = "Transactions sorted successfully by size. " +
                    "Comparisons: " + comparisons.get() + ", Swaps: " + swaps.get() +
                    ". Execution time: " + executionTime + " milliseconds";

            Map<String, Object> response = new HashMap<>();
            response.put("message", successMessage);
            response.put("sortedTransactions", transactions);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            JSONObject errorBody = new JSONObject();
            errorBody.put("status", "Failed to fetch or sort transactions: " + e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private JSONArray getCryptoMarketDataFromAPI(String symbolId) throws Exception {
        String today = new Date().toString().substring(0, 10);
        ZonedDateTime oneWeekAgo = ZonedDateTime.now(ZoneOffset.UTC).minus(Duration.ofDays(7));
        String startTime = oneWeekAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        int limit = 10;

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

    private List<Transaction> convertToTransactionList(JSONArray cryptoAPIData) {
        List<Transaction> transactions = new ArrayList<>();
        for (Object obj : cryptoAPIData) {
            JSONObject transactionData = (JSONObject) obj;
            double size = Double.parseDouble(transactionData.get("size").toString());
            Transaction transaction = new Transaction(size);
            transactions.add(transaction);
        }
        return transactions;
    }

    private void bubbleSortTransactionsBySize(List<Transaction> transactions, AtomicInteger comparisons, AtomicInteger swaps) {
        int n = transactions.size();
        boolean swapped;
    
        do {
            swapped = false;
    
            for (int i = 0; i < n - 1; i++) {
                comparisons.incrementAndGet(); // Increment comparison count
    
                if (transactions.get(i).getSize() > transactions.get(i + 1).getSize()) {
                    Transaction temp = transactions.get(i);
                    transactions.set(i, transactions.get(i + 1));
                    transactions.set(i + 1, temp);
                    swapped = true;
    
                    swaps.incrementAndGet(); // Increment swap count
                }
            }
    
            n--;
        } while (swapped);
    }
    

    private void selectionSortTransactionsBySize(List<Transaction> transactions, AtomicInteger comparisons, AtomicInteger swaps) {
        int n = transactions.size();
    
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                comparisons.incrementAndGet(); // Increment comparison count
                if (transactions.get(j).getSize() < transactions.get(minIndex).getSize()) {
                    minIndex = j;
                }
            }
    
            // Swap the found minimum element with the first element
            Transaction temp = transactions.get(minIndex);
            transactions.set(minIndex, transactions.get(i));
            transactions.set(i, temp);
            
            swaps.incrementAndGet(); // Increment swap count
        }
    }
    
    
    
    
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

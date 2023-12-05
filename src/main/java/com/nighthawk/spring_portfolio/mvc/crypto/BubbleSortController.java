package com.nighthawk.spring_portfolio.mvc.crypto;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
public class BubbleSortController extends CryptoApiController {

    @GetMapping("/bubble/{symbolId}")
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
}




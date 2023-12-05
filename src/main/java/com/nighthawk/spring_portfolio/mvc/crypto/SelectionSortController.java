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
public class SelectionSortController extends CryptoApiController {

    @GetMapping("/selection/{symbolId}")
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

            String successMessage = "Transactions sorted successfully by size using Selection Sort. " +
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
}

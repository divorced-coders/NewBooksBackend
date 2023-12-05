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
public class InsertionSortController extends CryptoApiController {

    @GetMapping("/insertion/{symbolId}")
    public ResponseEntity<Object> sortByInsertion(@PathVariable String symbolId) {
        try {
            long startTime = System.currentTimeMillis();

            JSONArray cryptoAPIData = getCryptoMarketDataFromAPI(symbolId);

            List<Transaction> transactions = convertToTransactionList(cryptoAPIData);

            AtomicInteger comparisons = new AtomicInteger(0);
            AtomicInteger shifts = new AtomicInteger(0);

            insertionSortTransactionsBySize(transactions, comparisons, shifts);

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            String successMessage = "Transactions sorted successfully by size using Insertion Sort. " +
                    "Comparisons: " + comparisons.get() + ", Shifts: " + shifts.get() +
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

    private void insertionSortTransactionsBySize(List<Transaction> transactions, AtomicInteger comparisons, AtomicInteger shifts) {
        int n = transactions.size();

        for (int i = 1; i < n; i++) {
            Transaction key = transactions.get(i);
            int j = i - 1;

            while (j >= 0 && transactions.get(j).getSize() > key.getSize()) {
                comparisons.incrementAndGet();
                transactions.set(j + 1, transactions.get(j));
                j--;
                shifts.incrementAndGet();
            }

            transactions.set(j + 1, key);
        }
    }
}

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
public class HeapSortController extends CryptoApiController {

    @GetMapping("/heap/{symbolId}")
    public ResponseEntity<Object> sortByHeap(@PathVariable String symbolId) {
        try {
            long startTime = System.currentTimeMillis();

            JSONArray cryptoAPIData = getCryptoMarketDataFromAPI(symbolId);

            List<Transaction> transactions = convertToTransactionList(cryptoAPIData);

            AtomicInteger comparisons = new AtomicInteger(0);
            AtomicInteger swaps = new AtomicInteger(0);

            heapSortTransactionsBySize(transactions, comparisons, swaps);

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            String successMessage = "Transactions sorted successfully by size using Heap Sort. " +
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

    private void heapSortTransactionsBySize(List<Transaction> transactions, AtomicInteger comparisons, AtomicInteger swaps) {
        int n = transactions.size();

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(transactions, n, i, comparisons, swaps);
        }

        for (int i = n - 1; i > 0; i--) {
            swap(transactions, 0, i, swaps);
            heapify(transactions, i, 0, comparisons, swaps);
        }
    }

    private void heapify(List<Transaction> transactions, int n, int i, AtomicInteger comparisons, AtomicInteger swaps) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && transactions.get(left).getSize() > transactions.get(largest).getSize()) {
            largest = left;
        }

        if (right < n && transactions.get(right).getSize() > transactions.get(largest).getSize()) {
            largest = right;
        }

        comparisons.incrementAndGet();

        if (largest != i) {
            swap(transactions, i, largest, swaps);
            heapify(transactions, n, largest, comparisons, swaps);
        }
    }

    private void swap(List<Transaction> transactions, int i, int j, AtomicInteger swaps) {
        Transaction temp = transactions.get(i);
        transactions.set(i, transactions.get(j));
        transactions.set(j, temp);
        swaps.incrementAndGet();
    }
}

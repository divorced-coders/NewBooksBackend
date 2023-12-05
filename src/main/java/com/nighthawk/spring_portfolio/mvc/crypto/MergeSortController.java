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
public class MergeSortController extends CryptoApiController {

    @GetMapping("/merge/{symbolId}")
    public ResponseEntity<Object> sortByMerge(@PathVariable String symbolId) {
        try {
            long startTime = System.currentTimeMillis();

            JSONArray cryptoAPIData = getCryptoMarketDataFromAPI(symbolId);

            List<Transaction> transactions = convertToTransactionList(cryptoAPIData);

            AtomicInteger comparisons = new AtomicInteger(0);
            AtomicInteger merges = new AtomicInteger(0);

            mergeSortTransactionsBySize(transactions, comparisons, merges);

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            String successMessage = "Transactions sorted successfully by size using Merge Sort. " +
                    "Comparisons: " + comparisons.get() + ", Merges: " + merges.get() +
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

    private void mergeSortTransactionsBySize(List<Transaction> transactions, AtomicInteger comparisons, AtomicInteger merges) {
        int n = transactions.size();

        if (n > 1) {
            int mid = n / 2;
            List<Transaction> left = transactions.subList(0, mid);
            List<Transaction> right = transactions.subList(mid, n);

            mergeSortTransactionsBySize(left, comparisons, merges);
            mergeSortTransactionsBySize(right, comparisons, merges);

            merge(transactions, left, right, comparisons, merges);
        }
    }

    private void merge(List<Transaction> transactions, List<Transaction> left, List<Transaction> right, AtomicInteger comparisons, AtomicInteger merges) {
        int i = 0, j = 0, k = 0;
        int n1 = left.size();
        int n2 = right.size();

        while (i < n1 && j < n2) {
            comparisons.incrementAndGet();
            if (left.get(i).getSize() <= right.get(j).getSize()) {
                transactions.set(k++, left.get(i++));
            } else {
                transactions.set(k++, right.get(j++));
            }
        }

        while (i < n1) {
            transactions.set(k++, left.get(i++));
            merges.incrementAndGet();
        }

        while (j < n2) {
            transactions.set(k++, right.get(j++));
            merges.incrementAndGet();
        }
    }
}

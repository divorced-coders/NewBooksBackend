package com.nighthawk.spring_portfolio.mvc.crypto;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@CrossOrigin(origins = {"http://127.0.0.1:4000"})
public class SortController extends CryptoApiController {

    @GetMapping("/compareSort/{symbolId}/{algorithm1}/{algorithm2}")
    public ResponseEntity<Object> compareSortAlgorithms(@PathVariable String symbolId,
                                                        @PathVariable String algorithm1,
                                                        @PathVariable String algorithm2) {
        try {
            long startTimeAlgorithm1 = System.currentTimeMillis();

            // Run first sorting algorithm
            JSONArray cryptoAPIData1 = getCryptoMarketDataFromAPI(symbolId);
            List<Transaction> transactions1 = convertToTransactionList(cryptoAPIData1);
            AtomicInteger comparisons1 = new AtomicInteger(0);
            AtomicInteger swaps1 = new AtomicInteger(0);
            SortingAlgorithm sortingAlgorithm1 = getSortingAlgorithm(algorithm1);
            sortingAlgorithm1.sort(transactions1, Comparator.comparing(Transaction::getSize), comparisons1, swaps1);

            long endTimeAlgorithm1 = System.currentTimeMillis();
            long executionTimeAlgorithm1 = endTimeAlgorithm1 - startTimeAlgorithm1;

            long startTimeAlgorithm2 = System.currentTimeMillis();

            // Run second sorting algorithm
            JSONArray cryptoAPIData2 = getCryptoMarketDataFromAPI(symbolId);
            List<Transaction> transactions2 = convertToTransactionList(cryptoAPIData2);
            AtomicInteger comparisons2 = new AtomicInteger(0);
            AtomicInteger swaps2 = new AtomicInteger(0);
            SortingAlgorithm sortingAlgorithm2 = getSortingAlgorithm(algorithm2);
            sortingAlgorithm2.sort(transactions2, Comparator.comparing(Transaction::getSize), comparisons2, swaps2);

            long endTimeAlgorithm2 = System.currentTimeMillis();
            long executionTimeAlgorithm2 = endTimeAlgorithm2 - startTimeAlgorithm2;

            // Compare execution times
            String comparisonResult;
            if (executionTimeAlgorithm1 < executionTimeAlgorithm2) {
                comparisonResult = algorithm1 + " is faster than " + algorithm2;
            } else if (executionTimeAlgorithm1 > executionTimeAlgorithm2) {
                comparisonResult = algorithm2 + " is faster than " + algorithm1;
            } else {
                comparisonResult = algorithm1 + " and " + algorithm2 + " have the same execution time";
            }

            Map<String, Object> response = new HashMap<>();
            response.put("comparisonResult", comparisonResult);
            response.put(algorithm1, "Execution time: " + executionTimeAlgorithm1 + " milliseconds");
            response.put(algorithm2, "Execution time: " + executionTimeAlgorithm2 + " milliseconds");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            JSONObject errorBody = new JSONObject();
            errorBody.put("status", "Failed to fetch or compare sorting algorithms: " + e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sort/{symbolId}/{algorithm}")
    public ResponseEntity<Object> sortTransactions(@PathVariable String symbolId, @PathVariable String algorithm) {
        try {
            long startTime = System.currentTimeMillis();

            JSONArray cryptoAPIData = getCryptoMarketDataFromAPI(symbolId);
            List<Transaction> transactions = convertToTransactionList(cryptoAPIData);

            AtomicInteger comparisons = new AtomicInteger(0);
            AtomicInteger swaps = new AtomicInteger(0);

            SortingAlgorithm sortingAlgorithm = getSortingAlgorithm(algorithm);
            sortingAlgorithm.sort(transactions, Comparator.comparing(Transaction::getSize), comparisons, swaps);

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            String successMessage = "Transactions sorted successfully by " + algorithm + ". " +
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

    private SortingAlgorithm getSortingAlgorithm(String algorithm) {
        //cases for each algorithm
        switch (algorithm.toLowerCase()) {
            case "bubble":
                return new BubbleSort();
            case "selection":
                return new SelectionSort();
            case "heap":
                return new HeapSort();
            case "merge":
                return new MergeSort();
            case "insertion":
                return new InsertionSort();
            default:
                throw new IllegalArgumentException("Unsupported sorting algorithm: " + algorithm);
        }
    }
}

interface SortingAlgorithm {
    <T> void sort(List<T> list, Comparator<T> comparator, AtomicInteger comparisons, AtomicInteger swaps);
}

class BubbleSort implements SortingAlgorithm {
    @Override
    public <T> void sort(List<T> list, Comparator<T> comparator, AtomicInteger comparisons, AtomicInteger swaps) { //List<T> generic list of objects, Comparator<T> import comparing, AtomicIntger stuff: timing
        int n = list.size();
        boolean swapped;

        do {
            swapped = false;

            for (int i = 0; i < n - 1; i++) {
                comparisons.incrementAndGet();

                if (comparator.compare(list.get(i), list.get(i + 1)) > 0) { //using generics
                    T temp = list.get(i);
                    list.set(i, list.get(i + 1));
                    list.set(i + 1, temp);
                    swapped = true;

                    swaps.incrementAndGet();
                }
            }

            n--;
        } while (swapped);
    }
}

class SelectionSort implements SortingAlgorithm {
    @Override
    public <T> void sort(List<T> list, Comparator<T> comparator, AtomicInteger comparisons, AtomicInteger swaps) {
        int n = list.size();

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < n; j++) {
                comparisons.incrementAndGet(); // Increment comparison count

                if (comparator.compare(list.get(j), list.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }

            // Swap the found minimum element with the current element
            T temp = list.get(minIndex);
            list.set(minIndex, list.get(i));
            list.set(i, temp);

            swaps.incrementAndGet(); // Increment swap count
        }
    }
}

class HeapSort implements SortingAlgorithm {
    @Override
    public <T> void sort(List<T> list, Comparator<T> comparator, AtomicInteger comparisons, AtomicInteger swaps) {
        int n = list.size();

        // Build heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(list, n, i, comparator, comparisons);
        }

        // Extract elements from the heap
        for (int i = n - 1; i > 0; i--) {
            // Swap the root (maximum element) with the last element
            T temp = list.get(0);
            list.set(0, list.get(i));
            list.set(i, temp);

            swaps.incrementAndGet(); // Increment swap count

            // Heapify the reduced heap
            heapify(list, i, 0, comparator, comparisons);
        }
    }

    public <T> void heapify(List<T> list, int n, int i, Comparator<T> comparator, AtomicInteger comparisons) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && comparator.compare(list.get(left), list.get(largest)) > 0) {
            largest = left;
        }

        if (right < n && comparator.compare(list.get(right), list.get(largest)) > 0) {
            largest = right;
        }

        if (largest != i) {
            // Swap and continue to heapify
            T temp = list.get(i);
            list.set(i, list.get(largest));
            list.set(largest, temp);

            // Increment swap count (if needed)
            comparisons.incrementAndGet();

            heapify(list, n, largest, comparator, comparisons);
        }
    }
}

class MergeSort implements SortingAlgorithm {
    @Override
    public <T> void sort(List<T> list, Comparator<T> comparator, AtomicInteger comparisons, AtomicInteger swaps) {
        if (list.size() > 1) {
            int mid = list.size() / 2;

            List<T> left = new ArrayList<>(list.subList(0, mid));
            List<T> right = new ArrayList<>(list.subList(mid, list.size()));

            sort(left, comparator, comparisons, swaps);
            sort(right, comparator, comparisons, swaps);

            merge(list, left, right, comparator, comparisons, swaps);
        }
    }

    private <T> void merge(List<T> list, List<T> left, List<T> right, Comparator<T> comparator, AtomicInteger comparisons, AtomicInteger swaps) {
        int i = 0, j = 0, k = 0;

        while (i < left.size() && j < right.size()) {
            comparisons.incrementAndGet(); // Increment comparison count

            if (comparator.compare(left.get(i), right.get(j)) <= 0) {
                list.set(k++, left.get(i++));
            } else {
                list.set(k++, right.get(j++));
            }
        }

        while (i < left.size()) {
            list.set(k++, left.get(i++));
        }

        while (j < right.size()) {
            list.set(k++, right.get(j++));
        }
    }
}

class InsertionSort implements SortingAlgorithm {
    @Override
    public <T> void sort(List<T> list, Comparator<T> comparator, AtomicInteger comparisons, AtomicInteger swaps) {
        int n = list.size();

        for (int i = 1; i < n; ++i) {
            T key = list.get(i);
            int j = i - 1;

            while (j >= 0 && comparator.compare(list.get(j), key) > 0) {
                comparisons.incrementAndGet(); // Increment comparison count

                list.set(j + 1, list.get(j));
                j = j - 1;
            }

            list.set(j + 1, key);

            swaps.incrementAndGet(); // Increment swap count
        }
    }
}
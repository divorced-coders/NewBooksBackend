package com.nighthawk.spring_portfolio.mvc.crypto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data  // Annotations to simplify writing code (ie constructors, setters)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@TypeDef(name="json", typeClass = JsonType.class)
public class SortingMethods {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int[] transactions;

    private void insertionSort(int[] transactions) {
        int n = transactions.length;
        for (int i = 1; i < n; ++i) {
            int key = transactions[i];
            int j = i - 1;

            // Move elements of array[0..i-1] that are greater than key
            // to one position ahead of their current position
            while (j >= 0 && transactions[j] > key) {
                transactions[j + 1] = transactions[j];
                j = j - 1;
            }
            transactions[j + 1] = key;
        }
    }

    //Merge
    public static void mergeSort(int[] array) {
        int n = array.length;
        if (n > 1) {
            int mid = n / 2;

            // Create left and right subarrays
            int[] left = new int[mid];
            int[] right = new int[n - mid];

            // Populate left and right subarrays
            System.arraycopy(array, 0, left, 0, mid);
            System.arraycopy(array, mid, right, 0, n - mid);

            // Recursively sort the left and right subarrays
            mergeSort(left);
            mergeSort(right);

            // Merge the sorted left and right subarrays
            merge(array, left, right);
        }
    }
    // Merge method to combine two sorted arrays
    public static void merge(int[] array, int[] left, int[] right) {
        int i = 0, j = 0, k = 0;
        int leftLength = left.length;
        int rightLength = right.length;
        // Compare elements from left and right subarrays and merge them
        while (i < leftLength && j < rightLength) {
            if (left[i] <= right[j]) {
                array[k++] = left[i++];
            } else {
                array[k++] = right[j++];
            }
        }
        // Copy remaining elements from left subarray, if any
        while (i < leftLength) {
            array[k++] = left[i++];
        }
        // Copy remaining elements from right subarray, if any
        while (j < rightLength) {
            array[k++] = right[j++];
        }
    }

    //Heap
    public static void heapSort(int[] array) {
        int n = array.length;

        // Build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(array, n, i);
        }

        // Extract elements from the heap one by one
        for (int i = n - 1; i > 0; i--) {
            // Swap the root (maximum element) with the last element
            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;

            // Call heapify on the reduced heap
            heapify(array, i, 0);
        }
    }

    // To heapify a subtree rooted with node i which is an index in array[]
    public static void heapify(int[] array, int n, int i) {
        int largest = i; // Initialize largest as root
        int leftChild = 2 * i + 1;
        int rightChild = 2 * i + 2;

        // If left child is larger than root
        if (leftChild < n && array[leftChild] > array[largest]) {
            largest = leftChild;
        }

        // If right child is larger than largest so far
        if (rightChild < n && array[rightChild] > array[largest]) {
            largest = rightChild;
        }

        // If largest is not the root
        if (largest != i) {
            // Swap i and largest
            int swap = array[i];
            array[i] = array[largest];
            array[largest] = swap;

            // Recursively heapify the affected sub-tree
            heapify(array, n, largest);
        }
    }

    //Radix
    public static void radixSort(int[] array) {
        // Find the maximum number to know the number of digits
        int max = getMax(array);

        // Do counting sort for every digit
        for (int exp = 1; max / exp > 0; exp *= 10) {
            countingSort(array, exp);
        }
    }

    private static int getMax(int[] array) {
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    private static void countingSort(int[] array, int exp) {
        int n = array.length;
        int[] output = new int[n];
        int[] count = new int[10];
        Arrays.fill(count, 0);

        // Store count of occurrences in count[]
        for (int i = 0; i < n; i++) {
            count[(array[i] / exp) % 10]++;
        }

        // Change count[i] so that count[i] contains the actual
        // position of this digit in output[]
        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }

        // Build the output array
        for (int i = n - 1; i >= 0; i--) {
            output[count[(array[i] / exp) % 10] - 1] = array[i];
            count[(array[i] / exp) % 10]--;
        }

        // Copy the output array to array[], so that array[] now
        // contains sorted numbers according to the current digit
        System.arraycopy(output, 0, array, 0, n);
    }
}
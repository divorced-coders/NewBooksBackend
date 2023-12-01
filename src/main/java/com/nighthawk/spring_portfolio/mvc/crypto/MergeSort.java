package com.nighthawk.spring_portfolio.mvc.crypto;

public //Merge
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
//merge unit helps call for the crypto transaction
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

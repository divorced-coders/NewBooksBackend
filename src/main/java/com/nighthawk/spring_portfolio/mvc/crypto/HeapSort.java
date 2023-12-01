package com.nighthawk.spring_portfolio.mvc.crypto;
public class HeapSort {
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
            heapify(array, n, largest);
        }
    } 
}

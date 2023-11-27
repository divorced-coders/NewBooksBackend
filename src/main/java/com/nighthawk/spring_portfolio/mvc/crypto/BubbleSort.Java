package com.nighthawk.spring_portfolio.mvc.crypto;

public class BubbleSort {

    public static void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-i-1; j++) {
                if (arr[j] > arr[j+1]) {
                    // Swap arr[j] and arr[j+1]
                    int temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        int[] arrayToSort = {64, 34, 25, 12, 22, 11, 90};

        System.out.println("Array before sorting:");
        printArray(arrayToSort);

        sort(arrayToSort);

        System.out.println("\nArray after sorting:");
        printArray(arrayToSort);
    }

    private static void printArray(int[] arr) {
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }
}

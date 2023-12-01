package com.nighthawk.spring_portfolio.mvc.crypto;
public class Bubblesort {
   //Bubble
   public static void bubbleSort(int[] array) {
    int n = array.length;
    boolean swapped;
    for (int i = 0; i < n - 1; i++) {
        swapped = false;
        for (int j = 0; j < n - i - 1; j++) {
            if (array[j] > array[j + 1]) {
                // Swap array[j] and array[j+1]
                int temp = array[j];
                array[j] = array[j + 1];
                array[j + 1] = temp;

                swapped = true;
            }
        }
        if (!swapped) {
            break;
        }
    }
}

}

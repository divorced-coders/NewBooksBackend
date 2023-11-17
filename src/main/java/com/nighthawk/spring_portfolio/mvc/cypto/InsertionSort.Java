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
public class InsertionSort {
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
}
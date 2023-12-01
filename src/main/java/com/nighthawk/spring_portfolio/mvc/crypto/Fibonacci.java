package com.nighthawk.spring_portfolio.mvc.crypto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public abstract class Fibonacci {

    public abstract long calculateFibonacci(int n);
}

class RecursiveFibonacciCalculator extends Fibonacci {

    @Override
    public long calculateFibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return calculateFibonacci(n - 1) + calculateFibonacci(n - 2);
    }
}

class IterativeFibonacciCalculator extends Fibonacci {

    @Override
    public long calculateFibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        long fib = 1;
        long prev = 0;
        for (int i = 2; i <= n; i++) {
            long temp = fib;
            fib = fib + prev;
            prev = temp;
        }
        return fib;
    }
}

@RestController
@RequestMapping("/api/fibonacci")
class FibonacciController {

    private final Fibonacci fibonacciCalculator;

    public FibonacciController(Fibonacci fibonacciCalculator) {
        this.fibonacciCalculator = fibonacciCalculator;
    }

    @GetMapping("/{n}")
    public long calculateFibonacci(@PathVariable int n) {
        return fibonacciCalculator.calculateFibonacci(n);
    }
}

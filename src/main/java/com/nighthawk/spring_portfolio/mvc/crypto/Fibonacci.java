package com.nighthawk.spring_portfolio.mvc.crypto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fibonacci")
public class Fibonacci {

    @GetMapping("/{n}")
    public long calculateFibonacci(@PathVariable int n) {
        return fibonacci(n);
    }

    @GetMapping("/sequence")
    public long[] getFibonacciSequence(@RequestParam int start, @RequestParam int count) {
        long[] sequence = new long[count];
        for (int i = 0; i < count; i++) {
            sequence[i] = fibonacci(start + i);
        }
        return sequence;
    }

    private long fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
}

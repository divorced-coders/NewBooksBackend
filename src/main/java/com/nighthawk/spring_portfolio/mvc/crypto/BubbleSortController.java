package com.nighthawk.spring_portfolio.mvc.crypto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



@RestController
@RequestMapping("/sorting")
public class BubbleSortController {

    private final CryptoApiController cryptoApiController;

    @Autowired
    private final SortingMethods SortingMethods;

    @Autowired
    public BubbleSortController(CryptoApiController cryptoApiController) {
        this.cryptoApiController = cryptoApiController;
    }

    @GetMapping("/bubble")
    public ResponseEntity<double[]> bubbleSortSizeFromAPI() {
        try {
            // Fetch data using CryptoApiController
            JSONArray cryptoAPIData = cryptoApiController.getCryptoMarketData(); // Assuming this method fetches data

            // Convert JSONArray 'size' values to an array of doubles for sorting
            double[] sizesToSort = new double[cryptoAPIData.size()];
            for (int i = 0; i < cryptoAPIData.size(); i++) {
                JSONObject trade = (JSONObject) cryptoAPIData.get(i);
                sizesToSort[i] = ((Number) trade.get("size")).doubleValue();
            }

            // Apply bubble sort on sizes
            SortingMethods.bubbleSortDouble(sizesToSort); // Use a double version of bubble sort

            return new ResponseEntity<>(sizesToSort, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.nighthawk.spring_portfolio.mvc.crypto;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crypto")
public class CryptoApiController {
    private Object body; // Use Object type to handle both JSONObject and JSONArray
    private HttpStatus status;
    String last_run = null;

    @GetMapping("/market")
    public ResponseEntity<Object> getCryptoMarketData() {
        String today = new Date().toString().substring(0, 10);
        if (last_run == null || !today.equals(last_run)) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://rest.coinapi.io/v1/assets"))
                        .header("x-coinapi-key", "A45C5875-F234-49DA-BED1-E30E1E15EA9E")
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

                // Parse the response
                Object parsedResponse = new JSONParser().parse(response.body());
                
                // Check if the parsed response is a JSONArray or JSONObject
                if (parsedResponse instanceof JSONArray) {
                    this.body = (JSONArray) parsedResponse;
                } else if (parsedResponse instanceof JSONObject) {
                    this.body = (JSONObject) parsedResponse;
                }

                this.status = HttpStatus.OK;
                this.last_run = today;
            } catch (Exception e) {
                JSONObject errorBody = new JSONObject();
            errorBody.put("status", "CoinAPI failure: " + e.getMessage());

                this.body = errorBody;
                this.status = HttpStatus.INTERNAL_SERVER_ERROR;
                this.last_run = null;
            }
        }

        return new ResponseEntity<>(body, status);
    }
}
//clear merge
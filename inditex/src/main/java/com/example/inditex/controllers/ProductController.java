package com.example.inditex.controllers;


import com.example.inditex.exceptions.HttpClientException;
import com.example.inditex.services.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("product/{productId}/similar")
    public String getDetailsOfSimilarProducts(@PathVariable String productId, HttpServletResponse response) {
        try {
            var products = productService.getDetailsOfSimilarProducts(productId);
            if (products.isEmpty()) {
                HttpClientException productNotFound = new HttpClientException(404, "Not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return returnError(productNotFound);
            } else {
                return new ObjectMapper().writeValueAsString(products);
            }
        } catch (HttpClientException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return returnError(e);
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing to JSON: " + e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private String returnError(HttpClientException e) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("status", String.valueOf(e.getCode()));
            map.put("error", e.getMessage());
            map.put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
            return new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException ex) {
            System.err.println("Error parsing to JSON: " + e);
            throw new RuntimeException(ex);
        }

    }
}
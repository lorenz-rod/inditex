package com.example.inditex.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    @GetMapping("product/{productId}/similar")
    public String getSimilarProducts(@PathVariable Integer id) {
        //return customerRepository.findCustomerById(id);
        System.out.println("TEST");
        return "Producto";
    }

    @GetMapping( "/hello" )
   public String echo() {
      return "Hello World!";
   }
}
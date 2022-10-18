package com.example.inditex.controllers;

import com.example.inditex.dto.Product;
import com.example.inditex.exceptions.HttpClientException;
import com.example.inditex.services.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService productService;

    @BeforeAll
    public static void before() {

    }

    @Test
    void getDetailsOfSimilarProducts_Success() throws HttpClientException {
        List<Product> mockAnswer = new ArrayList<>();
        Product product1 = new Product("1","Shirt",100.10,false);
        mockAnswer.add(product1);
        Product product2 = new Product("2","Pants",99.10,true);
        mockAnswer.add(product2);
        given(productService.getDetailsOfSimilarProducts("3")).willReturn(mockAnswer);
        try {
            mvc.perform(get("/product/3/similar")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is(product1.getName())))
                    .andExpect(jsonPath("$[1].name", is(product2.getName())));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getDetailsOfSimilarProducts_EmptyResponse() throws HttpClientException {
        List<Product> mockAnswer = new ArrayList<>();
        given(productService.getDetailsOfSimilarProducts("3")).willReturn(mockAnswer);
        try {
            mvc.perform(get("/product/3/similar")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("status", is("404")))
                    .andExpect(jsonPath("error", is("Not found")));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getDetailsOfSimilarProducts_ErrorHttpClientException() throws HttpClientException {
        List<Product> mockAnswer = new ArrayList<>();
        given(productService.getDetailsOfSimilarProducts("3")).willThrow(new HttpClientException(500,"Internal server error"));
        try {
            mvc.perform(get("/product/3/similar")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("status", is("500")))
                    .andExpect(jsonPath("error", is("Internal server error")));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

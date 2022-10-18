package com.example.inditex.services;

import com.example.inditex.dto.Product;
import com.example.inditex.exceptions.HttpClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ProductService {


    private static final String PRODUCT_URL = "http://localhost:3001/product/";
	private static final String SIMILAR_IDS_ENDPOINT = "/similarids";
	@Autowired
	RestTemplate restTemplate;

	/*@Autowired
	private CircuitBreakerFactory circuitBreakerFactory;

	 */

	public List<Product> getDetailsOfSimilarProducts(String productId) throws HttpClientException {
		try {
			ResponseEntity<String> response
					= restTemplate.getForEntity(PRODUCT_URL + productId + SIMILAR_IDS_ENDPOINT, String.class);
			List<String> similarIds = stringToList(response.getBody());
			List<CompletableFuture<Product>> products = new ArrayList<>();
			for(int i = 0; i<similarIds.size();i++) {
				CompletableFuture<Product> futureProduct = getDetailOfProduct(similarIds.get(i));
				if (futureProduct != null) {
					products.add(futureProduct);
				}
			}
			if (products.isEmpty()) {
				return null;
			}
			else {
				//Wait for every call to end and return the concrete products list
				 return products.stream().map(CompletableFuture::join).collect(Collectors.toList());
			}

		}  catch (ResourceAccessException e) {
			System.err.println("Timeout while attempting to get similars of product: " + productId);
			return null;
		}	catch (HttpClientErrorException e) {
			System.out.println("Error when requesting similar products for productId: " + productId);
			throw new HttpClientException(e.getStatusCode().value(), e.getStatusText());
		}
	}

	@Async
	CompletableFuture<Product> getDetailOfProduct(String productId) {
		try {
			Product product
					= restTemplate.getForObject(PRODUCT_URL + productId, Product.class);
			return CompletableFuture.completedFuture(product);
		} catch (ResourceAccessException e) {
			System.err.println("Timeout while attempting to get detail of product: " + productId);
			return null;
		}  catch (HttpServerErrorException e) {
			System.err.println("Remote error while attempting to get detail of product: " + productId + ". Error: " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.err.println("Unexpected error while attempting to get detail of product: " + productId + ". Error: " + e.getMessage());
			return null;
		}
	}

	private List<String> stringToList(String list) {
		List<String> result = new ArrayList<>();
		String cleanList = list.replaceAll("\\[", "").replaceAll("]", "");
		String[] splitted = cleanList.split(",");
		for(int i = 0; i < splitted.length; i++){
			result.add(splitted[i]);
		}
		return result;
	}

}
